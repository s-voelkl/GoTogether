package com.gotogether.backend.services;

import com.gotogether.backend.dto.ChallengeCreatedDTO;
import com.gotogether.backend.dto.ChallengeDTO;
import com.gotogether.backend.dto.ChallengeSortAttribute;
import com.gotogether.backend.dto.ChallengeVerificationDTO;
import com.gotogether.backend.mapper.ChallengeMapper;
import com.gotogether.backend.model.Challenge;
import com.gotogether.backend.model.Company;
import com.gotogether.backend.model.Location;
import com.gotogether.backend.model.Topic;
import com.gotogether.backend.model.User;
import com.gotogether.backend.repository.ChallengeRepository;
import com.gotogether.backend.repository.CompanyRepository;
import com.gotogether.backend.repository.TopicRepository;
import com.gotogether.backend.repository.UserRepository;

import lombok.RequiredArgsConstructor;

import net.glxn.qrgen.javase.QRCode;

import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChallengeService {

    private final ChallengeRepository repo;
    private final CompanyRepository companyRepo;
    private final TopicRepository topicRepo;
    private final UserRepository userRepo;

    private final ChallengeMapper challengeMapper;

    /** Default duration applied when the caller does not supply one. */
    private static final int DEFAULT_DURATION_MINUTES = 120;

    /** Default currency reward funded by the company when not specified. */
    private static final int DEFAULT_CURRENCY_REWARD = 100;

    /** Length of the alphanumeric verification code (matches the entity column). */
    private static final int VERIFICATION_CODE_LENGTH = 5;

    /** Character set used to build the verification code. */
    private static final String VERIFICATION_CODE_ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    /** Edge length (in pixels) of the rendered QR code PNG. */
    private static final int QR_CODE_SIZE_PX = 250;

    /** Cryptographically strong RNG for generating verification codes. */
    private static final SecureRandom RNG = new SecureRandom();

    /** Mean Earth radius in kilometers used by the Haversine formula. */
    private static final int EARTH_RADIUS_KM = 6371;

    /** Hard cap on results returned by {@link #getChallengesByFilter}. */
    private static final int MAX_RESULT_LIMIT = 100;

    /** Default page size when the caller does not specify a limit. */
    private static final int DEFAULT_RESULT_LIMIT = 10;

    /**
     * Maximum distance in meters between the user's current location and the
     * challenge location for a participation to be accepted.
     */
    private static final double MAX_PARTICIPATION_DISTANCE_METERS = 400.0;

    /**
     * Minimum amount of time, in seconds, that must have passed between two
     * subsequent participations of the same user. Acts as a basic abuse and
     * brute-force safeguard.
     */
    private static final long PARTICIPATION_COOLDOWN_SECONDS = 10; // good for testing

    /**
     * Calculates the great-circle distance (shortest "as the crow flies" path
     * along the Earth's surface) between two geographic points using the
     * <a href="https://en.wikipedia.org/wiki/Haversine_formula">Haversine
     * formula</a>.
     *
     * <p>
     * The formula treats the Earth as a perfect sphere of radius
     * {@value #EARTH_RADIUS_KM} km. It is numerically stable even for very
     * small distances, which makes it a standard choice for navigation,
     * geodesy, and location-based services.
     *
     * <h2>Mathematical background</h2>
     *
     * <p>
     * Let
     * <ul>
     * <li>{@code φ1, φ2} be the latitudes of point 1 and point 2 (in radians),</li>
     * <li>{@code λ1, λ2} be the longitudes of point 1 and point 2 (in
     * radians),</li>
     * <li>{@code Δφ = φ2 − φ1} the latitude difference,</li>
     * <li>{@code Δλ = λ2 − λ1} the longitude difference,</li>
     * <li>{@code R} the Earth radius, and</li>
     * <li>{@code hav(θ) = sin²(θ / 2)} the haversine function.</li>
     * </ul>
     *
     * <p>
     * The central angle {@code d / R} between the two points satisfies:
     * 
     * <pre>
     *     hav(d / R) = hav(Δφ) + cos(φ1) · cos(φ2) · hav(Δλ)
     * </pre>
     *
     * <p>
     * Solving for the distance {@code d} yields:
     * 
     * <pre>
     *     d = 2 · R · arcsin( sqrt( sin²(Δφ / 2) + cos(φ1) · cos(φ2) · sin²(Δλ / 2) ) )
     * </pre>
     *
     * <h2>Steps performed by this implementation</h2>
     * <ol>
     * <li>Convert all latitude/longitude inputs from degrees to radians.</li>
     * <li>Compute the latitude and longitude differences {@code Δφ} and
     * {@code Δλ}.</li>
     * <li>Evaluate the haversine term
     * {@code a = sin²(Δφ/2) + cos(φ1)·cos(φ2)·sin²(Δλ/2)}.</li>
     * <li>Derive the central angle {@code c = 2 · atan2(√a, √(1 − a))},
     * which is numerically equivalent to {@code 2 · arcsin(√a)} but more
     * stable near antipodal points.</li>
     * <li>Multiply by the Earth radius to obtain the distance in kilometers.</li>
     * </ol>
     *
     * <p>
     * <b>Note:</b> Because the Earth is an oblate ellipsoid rather than a
     * perfect sphere, the Haversine result deviates from exact geodetic
     * computations (e.g. Vincenty's formulae) by roughly 0.3 % to 0.5 %.
     * 
     * @see <a href="https://en.wikipedia.org/wiki/Haversine_formula">Haversine
     *      formula</a>
     *
     * @param lat1 latitude of the first point in decimal degrees
     * @param lon1 longitude of the first point in decimal degrees
     * @param lat2 latitude of the second point in decimal degrees
     * @param lon2 longitude of the second point in decimal degrees
     * @return the great-circle distance between the two points in meters
     */
    private double calculateLocationDistance(double lat1, double lon1, double lat2, double lon2) {
        // 1. Convert all coordinates from degrees to radians.
        double phi1 = Math.toRadians(lat1);
        double phi2 = Math.toRadians(lat2);
        double deltaPhi = Math.toRadians(lat2 - lat1);
        double deltaLambda = Math.toRadians(lon2 - lon1);

        // 2. Apply the haversine term: a = sin²(Δφ/2) + cos(φ1)·cos(φ2)·sin²(Δλ/2)
        double sinHalfDeltaPhi = Math.sin(deltaPhi / 2);
        double sinHalfDeltaLambda = Math.sin(deltaLambda / 2);
        double a = sinHalfDeltaPhi * sinHalfDeltaPhi
                + Math.cos(phi1) * Math.cos(phi2)
                        * sinHalfDeltaLambda * sinHalfDeltaLambda;

        // 3. Central angle in radians; atan2 form is numerically stable for all inputs.
        double centralAngle = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        // 4. Arc length on the sphere of radius R = distance in meters.
        return EARTH_RADIUS_KM * centralAngle * 1000;
    }

    public ChallengeDTO getChallengeById(UUID id) {
        return repo.findById(id)
                .map(challengeMapper::toDTO)
                .orElseThrow(() -> new RuntimeException("Challenge not found"));
    }

    /**
     * Returns all challenges that match every supplied filter. Filters are
     * combined with logical AND; "empty" values (null, blank string, {@code 0}
     * for numerics) disable the corresponding filter.
     * 
     * @note This method can be highly enhanced for performance.
     *       The current implementation is straightforward but inefficient, as it
     *       retrieves
     *       all challenges from the database and then applies the filters in
     *       memory.
     *       For test and demo purposes with small datasets, this is acceptable.
     *       However, for production use with larger datasets, it would be advisable
     *       to implement the filtering
     *       at the database level using JPA Specifications or QueryDSL to leverage
     *       database indexing and reduce
     *       memory usage. This would involve translating the filter criteria into a
     *       dynamic query that only retrieves
     *       the matching challenges directly from the database, rather than
     *       fetching all and filtering in Java.
     *       Also the geographic radius filter should be implemented with a rough
     *       estimation (pre-filtering with a bounding box)
     *       followed by the precise Haversine distance calculation to ensure good
     *       performance while maintaining accuracy.
     *
     * @param idLike                    case-insensitive substring matched against
     *                                  the
     *                                  challenge id; {@code null}/blank disables
     * @param titleLike                 case-insensitive substring matched against
     *                                  {@code title}; {@code null}/blank disables
     * @param descriptionLike           case-insensitive substring matched against
     *                                  {@code description}; {@code null}/blank
     *                                  disables
     * @param isArchived                exact match on the archived flag;
     *                                  {@code null}
     *                                  is treated as {@code false}
     * @param startTimeFrom             inclusive lower bound for {@code startTime};
     *                                  {@code null} disables
     * @param startTimeTo               inclusive upper bound for {@code startTime};
     *                                  {@code null} disables
     * @param minDurationMinutes        inclusive minimum {@code durationMinutes}
     * @param maxDurationMinutes        inclusive maximum {@code durationMinutes};
     *                                  {@code 0} disables
     * @param minCurrencyReward         minimum required {@code currency} reward;
     *                                  {@code 0} disables
     * @param minExperiencePointsReward minimum required {@code experiencePoints}
     *                                  reward; {@code 0} disables
     * @param maxSocialBattery          player's current social battery (0-100);
     *                                  keeps
     *                                  challenges whose required
     *                                  {@code maxSocialBattery} is affordable;
     *                                  {@code 0} disables
     * @param maxPlayers                maximum allowed {@code maxPlayers} cap on
     *                                  the
     *                                  challenge; {@code 0} disables (and
     *                                  challenges
     *                                  with {@code maxPlayers == 0} are treated as
     *                                  unlimited and always pass)
     * @param maxCurrentPlayers         maximum current participant count; {@code 0}
     *                                  disables
     * @param latitude                  latitude of the search center in decimal
     *                                  degrees ([-90, 90]); required (non-null)
     * @param longitude                 longitude of the search center in decimal
     *                                  degrees ([-180, 180]); required (non-null)
     * @param radiusMeters              search radius in meters; the location filter
     *                                  is only applied when
     *                                  {@code radiusMeters > 0}
     * @param hostCompanyNameLike       case-insensitive substring matched against
     *                                  the
     *                                  host company's name; {@code null}/blank
     *                                  disables
     * @param topicIds                  keep challenges sharing at least one topic
     *                                  with
     *                                  this list; {@code null}/empty disables
     * @return matching challenges as {@link ChallengeDTO}s
     * @throws RuntimeException if {@code latitude} or {@code longitude} is
     *                          {@code null} or out of range
     */
    public List<ChallengeDTO> getChallengesByFilter(
            String idLike,
            String titleLike,
            String descriptionLike,
            Boolean isArchived,
            LocalDateTime startTimeFrom,
            LocalDateTime startTimeTo,
            double minDurationMinutes,
            double maxDurationMinutes,
            int minCurrencyReward,
            int minExperiencePointsReward,
            int maxSocialBattery,
            int maxPlayers,
            int maxCurrentPlayers,
            Double latitude,
            Double longitude,
            double radiusMeters,
            String hostCompanyNameLike,
            List<UUID> topicIds,
            ChallengeSortAttribute sortBy1,
            boolean sort1Ascending,
            ChallengeSortAttribute sortBy2,
            boolean sort2Ascending,
            int limit) {

        // String values: check if the String can be found in the field.
        final String finalIdLike = (idLike == null) ? "" : idLike.trim().toLowerCase();
        final String finalTitleLike = (titleLike == null) ? "" : titleLike.trim().toLowerCase();
        final String finalDescriptionLike = (descriptionLike == null) ? "" : descriptionLike.trim().toLowerCase();
        final String finalHostCompanyNameLike = (hostCompanyNameLike == null) ? ""
                : hostCompanyNameLike.trim().toLowerCase();

        final List<UUID> finalTopicIds = (topicIds == null) ? List.of() : topicIds;
        final Boolean finalIsArchived = (isArchived == null) ? false : isArchived;
        final int finalMaxSocialBattery = Math.max(0, Math.min(100, maxSocialBattery));
        final int finalMinCurrencyReward = Math.max(0, minCurrencyReward);
        final int finalMinExperiencePointsReward = Math.max(0, minExperiencePointsReward);
        final int finalMaxPlayers = Math.max(0, maxPlayers);
        final int finalMaxCurrentPlayers = Math.max(0, maxCurrentPlayers);
        final double finalRadiusMeters = Math.max(0, radiusMeters);
        final double finalMinDurationMinutes = Math.max(0, minDurationMinutes);
        final double finalMaxDurationMinutes = Math.max(0, maxDurationMinutes);
        final LocalDateTime finalStartTimeFrom = startTimeFrom;
        final LocalDateTime finalStartTimeTo = startTimeTo;

        if (latitude == null) {
            throw new RuntimeException("Latitude must be provided");
        }
        if (longitude == null) {
            throw new RuntimeException("Longitude must be provided");
        }
        if (latitude < -90 || latitude > 90) {
            throw new RuntimeException("Latitude must be between -90 and 90 degrees");
        }
        if (longitude < -180 || longitude > 180) {
            throw new RuntimeException("Longitude must be between -180 and 180 degrees");
        }
        final double finalLatitude = latitude;
        final double finalLongitude = longitude;
        final boolean locationFilterActive = finalRadiusMeters > 0;

        // Build sort comparator: explicit sort attributes (up to 2) first,
        // then distance ascending as the default final tiebreaker.
        Comparator<Challenge> comparator = null;
        if (sortBy1 != null) {
            comparator = buildComparator(sortBy1, sort1Ascending, finalLatitude, finalLongitude);
        }
        if (sortBy2 != null) {
            Comparator<Challenge> c2 = buildComparator(sortBy2, sort2Ascending, finalLatitude, finalLongitude);
            comparator = (comparator == null) ? c2 : comparator.thenComparing(c2);
        }
        Comparator<Challenge> distanceAsc = buildComparator(
                ChallengeSortAttribute.DISTANCE, true, finalLatitude, finalLongitude);
        comparator = (comparator == null) ? distanceAsc : comparator.thenComparing(distanceAsc);

        // output limit: enforce hard cap and apply default if invalid value provided
        final int effectiveLimit = Math.min(MAX_RESULT_LIMIT,
                limit <= 0 ? DEFAULT_RESULT_LIMIT : limit);

        // Apply filters in memory (see method-level Javadoc for performance notes) and
        // sort with the built comparator.
        return repo.findAll().stream()
                // id substring match
                .filter(c -> finalIdLike.isEmpty()
                        || c.getId().toString().toLowerCase().contains(finalIdLike))
                // title contains
                .filter(c -> finalTitleLike.isEmpty()
                        || (c.getTitle() != null
                                && c.getTitle().toLowerCase().contains(finalTitleLike)))
                // description contains
                .filter(c -> finalDescriptionLike.isEmpty()
                        || (c.getDescription() != null
                                && c.getDescription().toLowerCase().contains(finalDescriptionLike)))
                // archived flag (exact match; defaults to false when not provided)
                .filter(c -> c.isArchived() == finalIsArchived)
                // time range
                .filter(c -> finalStartTimeFrom == null
                        || (c.getStartTime() != null && !c.getStartTime().isBefore(finalStartTimeFrom)))
                .filter(c -> finalStartTimeTo == null
                        || (c.getStartTime() != null && !c.getStartTime().isAfter(finalStartTimeTo)))
                // duration range (max == 0 means no upper limit)
                .filter(c -> finalMinDurationMinutes == 0 || c.getDurationMinutes() >= finalMinDurationMinutes)
                .filter(c -> finalMaxDurationMinutes == 0
                        || c.getDurationMinutes() <= finalMaxDurationMinutes)
                // minimum currency / xp reward (0 means no filter)
                .filter(c -> finalMinCurrencyReward == 0 || c.getCurrency() >= finalMinCurrencyReward)
                .filter(c -> finalMinExperiencePointsReward == 0
                        || c.getExperiencePoints() >= finalMinExperiencePointsReward)
                // social-battery: challenge should not require more social battery
                // than the player currently has; 0 means no filter
                .filter(c -> finalMaxSocialBattery == 0
                        || c.getMinSocialBattery() <= finalMaxSocialBattery)
                // capacity limit (0 means no filter; challenge.maxPlayers == 0 means unlimited)
                .filter(c -> finalMaxPlayers == 0
                        || c.getMaxPlayers() == 0
                        || c.getMaxPlayers() <= finalMaxPlayers)
                // current participant count
                .filter(c -> finalMaxCurrentPlayers == 0
                        || (c.getUsers() == null ? 0 : c.getUsers().size()) <= finalMaxCurrentPlayers)
                // host company name contains
                .filter(c -> finalHostCompanyNameLike.isEmpty()
                        || (c.getHost() != null
                                && c.getHost().getName() != null
                                && c.getHost().getName().toLowerCase().contains(finalHostCompanyNameLike)))
                // topic overlap of at least one topic
                .filter(c -> finalTopicIds.isEmpty()
                        || (c.getTopics() != null && c.getTopics().stream()
                                .anyMatch(t -> finalTopicIds.contains(t.getId()))))
                // geographic radius
                .filter(c -> !locationFilterActive
                        || (c.getLocation() != null
                                && calculateLocationDistance(finalLatitude, finalLongitude,
                                        c.getLocation().getLatitude(),
                                        c.getLocation().getLongitude()) <= finalRadiusMeters))
                // sort with the built comparator
                .sorted(comparator)
                // limit output to the effective limit after filtering and sorting
                .limit(effectiveLimit)
                // to DTO conversion
                .map(challengeMapper::toDTO)
                .toList();
    }

    private Comparator<Challenge> buildComparator(
            ChallengeSortAttribute attribute,
            boolean ascending,
            double centerLat,
            double centerLon) {
        Comparator<Challenge> base = switch (attribute) {
            case START_TIME -> Comparator.comparing(
                    Challenge::getStartTime, Comparator.nullsLast(Comparator.naturalOrder()));
            case DURATION -> Comparator.comparingInt(Challenge::getDurationMinutes);
            case CURRENCY_REWARD -> Comparator.comparingInt(Challenge::getCurrency);
            case EXPERIENCE_POINTS_REWARD -> Comparator.comparingInt(Challenge::getExperiencePoints);
            case MIN_SOCIAL_BATTERY -> Comparator.comparingInt(Challenge::getMinSocialBattery);
            case MAX_PLAYERS -> Comparator.comparingInt(Challenge::getMaxPlayers);
            case CURRENT_PLAYERS -> Comparator.comparingInt(
                    c -> c.getUsers() == null ? 0 : c.getUsers().size());
            case DISTANCE -> Comparator.comparingDouble(c -> c.getLocation() == null
                    ? Double.MAX_VALUE
                    : calculateLocationDistance(centerLat, centerLon,
                            c.getLocation().getLatitude(), c.getLocation().getLongitude()));
        };
        return ascending ? base : base.reversed();
    }

    public ChallengeVerificationDTO verifyChallenge(UUID id, String verificationCode) {
        var challenge = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Challenge not found"));

        if (challenge.getVerificationCode().equals(verificationCode)) {
            return challengeMapper.toVerificationDTO(challenge);
        } else {
            throw new RuntimeException("Invalid verification code");
        }
    }

    /**
     * Determines the experience points a challenge should award.
     *
     * <p>
     * The final implementation is expected to derive the value from the
     * challenge's properties (duration, difficulty, topics, ...). The current
     * placeholder returns a constant value so the rest of the create flow can
     * be implemented and tested independently.
     *
     * @return the experience points to assign to the challenge
     */
    int calculateExperiencePoints() {
        // TODO: replace with a real heuristic based on the challenge
        // properties (duration, social-battery cost, topics, ...).
        return 100;
    }

    /**
     * Generates a random alphanumeric verification code of length
     * {@value #VERIFICATION_CODE_LENGTH} using a cryptographically strong RNG.
     *
     * @return the newly generated code (upper-case letters and digits)
     */
    String generateVerificationCode() {
        StringBuilder sb = new StringBuilder(VERIFICATION_CODE_LENGTH);
        for (int i = 0; i < VERIFICATION_CODE_LENGTH; i++) {
            sb.append(VERIFICATION_CODE_ALPHABET.charAt(
                    RNG.nextInt(VERIFICATION_CODE_ALPHABET.length())));
        }
        return sb.toString();
    }

    /**
     * Renders the supplied payload as a QR code and returns it as a
     * Base64-encoded PNG image suitable for embedding via a {@code data:} URL
     * or returning inside a JSON response.
     *
     * <p>
     * Uses the <a href="https://github.com/kenglxn/QRGen">QRGen</a> library,
     * a thin builder-style wrapper around ZXing. The fluent chain is:
     * <ol>
     * <li>{@code QRCode.from(text)} — create a builder for the payload.</li>
     * <li>{@code .withSize(w, h)} — set the rendered edge length in pixels.</li>
     * <li>{@code .stream()} — render in-memory into a
     * {@link ByteArrayOutputStream}; PNG is QRGen's default output format,
     * so no explicit {@code .to(...)} call is required.</li>
     * </ol>
     *
     * @param payload the text to encode into the QR code
     * @return the Base64-encoded PNG bytes of the rendered QR code
     */
    String generateQrCodePngBase64(String payload) {
        ByteArrayOutputStream baos = QRCode.from(payload)
                .withSize(QR_CODE_SIZE_PX, QR_CODE_SIZE_PX)
                .stream();
        return Base64.getEncoder().encodeToString(baos.toByteArray());
    }

    /**
     * Creates a new challenge on behalf of an authenticated company.
     *
     * <p>
     * The company is authenticated via {@code companyEmail} /
     * {@code companyPassword}. The configured currency reward is transferred
     * from the company's balance to the challenge; the call fails (and no
     * state is mutated) when the company has insufficient funds.
     *
     * <p>
     * Optional parameters fall back to sensible defaults:
     * <ul>
     * <li>{@code description} → {@code title} when blank</li>
     * <li>{@code durationMinutes} → {@value #DEFAULT_DURATION_MINUTES}</li>
     * <li>{@code latitude} / {@code longitude} → the company's location</li>
     * <li>{@code currency} → {@value #DEFAULT_CURRENCY_REWARD}</li>
     * <li>{@code minSocialBattery} → 0</li>
     * <li>{@code maxPlayers} → 0 (unlimited)</li>
     * </ul>
     *
     * @return the new challenge's id and verification code
     * @throws RuntimeException on validation errors, missing references or
     *                          insufficient company funds
     */
    public ChallengeCreatedDTO createChallenge(
            String companyEmail,
            String companyPassword,
            String title,
            String description,
            LocalDateTime startTime,
            Integer durationMinutes,
            Double latitude,
            Double longitude,
            Integer currency,
            Integer minSocialBattery,
            Integer maxPlayers,
            List<UUID> topicIds) {

        // -------- authenticate company --------
        if (companyEmail == null || companyEmail.trim().isEmpty()) {
            throw new RuntimeException("Company email must not be empty.");
        }
        if (companyPassword == null || companyPassword.trim().isEmpty()) {
            throw new RuntimeException("Company password must not be empty.");
        }

        Company company = companyRepo.findByEmail(companyEmail.trim().toLowerCase());
        if (company == null) {
            throw new RuntimeException("No company found with email: " + companyEmail);
        }
        if (!company.getPassword().equals(companyPassword)) {
            throw new RuntimeException("Invalid company credentials.");
        }

        // -------- validate required inputs --------
        if (title == null || title.trim().isEmpty()) {
            throw new RuntimeException("Title must not be empty.");
        }
        if (startTime == null) {
            throw new RuntimeException("Start time must be provided.");
        }
        if (topicIds == null || topicIds.isEmpty()) {
            throw new RuntimeException("At least one topic must be provided.");
        }

        // -------- apply defaults --------
        // use title as description when description is blank
        String finalDescription = (description == null || description.trim().isEmpty())
                ? title.trim()
                : description.trim();
        // use default duration if not provided
        int finalDuration = (durationMinutes == null || durationMinutes <= 0)
                ? DEFAULT_DURATION_MINUTES
                : durationMinutes;
        // use small default currency if not provided
        int finalCurrency = (currency == null) ? DEFAULT_CURRENCY_REWARD : currency;
        if (finalCurrency < 0) {
            throw new RuntimeException("Currency reward must be non-negative: " + finalCurrency);
        }
        int finalMinSocialBattery = (minSocialBattery == null) ? 0 : minSocialBattery;
        if (finalMinSocialBattery < 0 || finalMinSocialBattery > 100) {
            throw new RuntimeException(
                    "minSocialBattery must be between 0 and 100: " + finalMinSocialBattery);
        }
        int finalMaxPlayers = (maxPlayers == null || maxPlayers < 0) ? 0 : maxPlayers;

        // -------- resolve location (fallback to company) --------
        Location location;
        if (latitude == null || longitude == null) {
            if (company.getLocation() == null) {
                throw new RuntimeException(
                        "Location not provided and host company has no location set.");
            }
            location = new Location(
                    company.getLocation().getLatitude(),
                    company.getLocation().getLongitude());
        } else {
            if (latitude < -90 || latitude > 90) {
                throw new RuntimeException(
                        "Latitude must be between -90 and 90: " + latitude);
            }
            if (longitude < -180 || longitude > 180) {
                throw new RuntimeException(
                        "Longitude must be between -180 and 180: " + longitude);
            }
            location = new Location(latitude, longitude);
        }

        // -------- resolve topics --------
        List<Topic> topics = new ArrayList<>(topicIds.size());
        for (UUID topicId : topicIds) {
            Topic topic = topicRepo.findById(topicId)
                    .orElseThrow(() -> new RuntimeException("Topic not found: " + topicId));
            topics.add(topic);
        }

        int experiencePoints = calculateExperiencePoints();
        String verificationCode = generateVerificationCode();

        // -------- transfer currency from company to challenge --------
        if (company.getCurrency() < finalCurrency) {
            throw new RuntimeException(
                    "Company has insufficient currency: has " + company.getCurrency()
                            + ", needs " + finalCurrency);
        }
        company.setCurrency(company.getCurrency() - finalCurrency);
        companyRepo.save(company);

        // -------- build and persist the challenge --------
        Challenge challenge = new Challenge();
        challenge.setTitle(title.trim());
        challenge.setDescription(finalDescription);
        challenge.setArchived(false);
        challenge.setStartTime(startTime);
        challenge.setLocation(location);
        challenge.setDurationMinutes(finalDuration);
        challenge.setCurrency(finalCurrency);
        challenge.setExperiencePoints(experiencePoints);
        challenge.setMinSocialBattery(finalMinSocialBattery);
        challenge.setVerificationCode(verificationCode);
        challenge.setMaxPlayers(finalMaxPlayers);
        challenge.setHost(company);
        challenge.setTopics(topics);
        challenge.setUsers(new ArrayList<>());

        Challenge saved = repo.save(challenge);

        return ChallengeCreatedDTO.builder()
                .id(saved.getId())
                .verificationCode(saved.getVerificationCode())
                .qrCodePngBase64(generateQrCodePngBase64(saved.getVerificationCode()))
                .build();
    }

    /**
     * Deletes a challenge and refunds its currency reward to the hosting
     * company (when the company still exists).
     *
     * <p>
     * The hosting company is authenticated via the supplied credentials and
     * must match the challenge's host. If the host record has been removed in
     * the meantime, the challenge is still deleted but no refund is issued.
     *
     * @param companyEmail    email of the authenticated company
     * @param companyPassword password of the authenticated company
     * @param challengeId     id of the challenge to delete
     * @return the id of the deleted challenge
     * @throws RuntimeException on authentication errors or when the challenge
     *                          does not exist / is not hosted by the company
     */
    public UUID deleteChallenge(String companyEmail, String companyPassword, UUID challengeId) {
        if (companyEmail == null || companyEmail.trim().isEmpty()) {
            throw new RuntimeException("Company email must not be empty.");
        }
        if (companyPassword == null || companyPassword.trim().isEmpty()) {
            throw new RuntimeException("Company password must not be empty.");
        }
        if (challengeId == null) {
            throw new RuntimeException("Challenge id must be provided.");
        }

        Company company = companyRepo.findByEmail(companyEmail.trim().toLowerCase());
        if (company == null) {
            throw new RuntimeException("No company found with email: " + companyEmail);
        }
        if (!company.getPassword().equals(companyPassword)) {
            throw new RuntimeException("Invalid company credentials.");
        }

        Challenge challenge = repo.findById(challengeId)
                .orElseThrow(() -> new RuntimeException("Challenge not found: " + challengeId));

        if (challenge.getHost() == null || !challenge.getHost().getId().equals(company.getId())) {
            throw new RuntimeException("Challenge is not hosted by the authenticated company.");
        }

        // Refund the challenge's currency to the host company when it still
        // exists in the database (re-read to avoid acting on a stale entity).
        companyRepo.findById(company.getId()).ifPresent(refundTarget -> {
            refundTarget.setCurrency(refundTarget.getCurrency() + challenge.getCurrency());
            companyRepo.save(refundTarget);
        });

        repo.delete(challenge);
        return challengeId;
    }

    /**
     * Registers an authenticated user as a participant of a challenge and
     * awards the challenge's currency and experience point rewards to the
     * user.
     *
     * <p>
     * The following invariants are enforced; any violation results in a
     * {@link RuntimeException} and no state is mutated:
     * <ol>
     * <li>{@code userEmail} / {@code userPassword} authenticate against an
     * existing user record.</li>
     * <li>The user's current location is within
     * {@value #MAX_PARTICIPATION_DISTANCE_METERS} meters of the challenge
     * location, computed via
     * {@link #calculateLocationDistance(double, double, double, double)}.</li>
     * <li>The challenge still has capacity: {@code maxPlayers == 0}
     * (unlimited) or the current participant count is strictly less than
     * {@code maxPlayers}.</li>
     * <li>The user has not participated in another challenge within the last
     * {@value #PARTICIPATION_COOLDOWN_SECONDS} minutes; the most recent
     * participation is approximated by the largest {@code startTime} among
     * the challenges the user is already enrolled in.</li>
     * <li>The supplied {@code verificationCode} matches the challenge's code
     * after trimming and lower-casing both sides.</li>
     * <li>The user is not already enrolled in this challenge.</li>
     * </ol>
     *
     * <p>
     * On success, the user is added to the challenge's participant list, the
     * challenge's {@code currency} and {@code experiencePoints} rewards are
     * added to the user's balances, and both the challenge and the user are
     * persisted.
     *
     * @param userEmail        email used to authenticate the user
     * @param userPassword     password used to authenticate the user
     * @param userLatitude     current user latitude in decimal degrees
     * @param userLongitude    current user longitude in decimal degrees
     * @param challengeId      id of the challenge to join
     * @param verificationCode verification code presented by the user
     * @return the id of the joined challenge
     * @throws RuntimeException if any of the invariants above is violated
     */
    public UUID participateInChallenge(
            String userEmail,
            String userPassword,
            Double userLatitude,
            Double userLongitude,
            UUID challengeId,
            String verificationCode) {

        // -------- validate inputs --------
        if (userEmail == null || userEmail.trim().isEmpty()) {
            throw new RuntimeException("User email must not be empty.");
        }
        if (userPassword == null || userPassword.trim().isEmpty()) {
            throw new RuntimeException("User password must not be empty.");
        }
        if (challengeId == null) {
            throw new RuntimeException("Challenge id must be provided.");
        }
        if (verificationCode == null || verificationCode.trim().isEmpty()) {
            throw new RuntimeException("Verification code must not be empty.");
        }
        if (userLatitude == null || userLongitude == null) {
            throw new RuntimeException("User location must be provided.");
        }
        if (userLatitude < -90 || userLatitude > 90) {
            throw new RuntimeException(
                    "User latitude must be between -90 and 90: " + userLatitude);
        }
        if (userLongitude < -180 || userLongitude > 180) {
            throw new RuntimeException(
                    "User longitude must be between -180 and 180: " + userLongitude);
        }

        // -------- authenticate user --------
        User user = userRepo.findByEmail(userEmail.trim().toLowerCase());
        if (user == null) {
            throw new RuntimeException("No user found with email: " + userEmail);
        }
        if (!user.getPassword().equals(userPassword)) {
            throw new RuntimeException("Invalid user credentials.");
        }

        // -------- load challenge --------
        Challenge challenge = repo.findById(challengeId)
                .orElseThrow(() -> new RuntimeException("Challenge not found: " + challengeId));

        // -------- distance check --------
        if (challenge.getLocation() == null) {
            throw new RuntimeException("Challenge has no location set.");
        }
        double distanceMeters = calculateLocationDistance(
                userLatitude, userLongitude,
                challenge.getLocation().getLatitude(),
                challenge.getLocation().getLongitude());
        if (distanceMeters >= MAX_PARTICIPATION_DISTANCE_METERS) {
            throw new RuntimeException(
                    "User is too far away from the challenge location: "
                            + ((long) distanceMeters) + " m");
        }

        // -------- capacity check --------
        List<User> participants = challenge.getUsers() == null
                ? new ArrayList<>()
                : challenge.getUsers();
        if (challenge.getMaxPlayers() > 0 && participants.size() >= challenge.getMaxPlayers()) {
            throw new RuntimeException("Challenge is already full.");
        }

        // -------- duplicate-participation check --------
        boolean alreadyJoined = participants.stream()
                .anyMatch(p -> p.getId() != null && p.getId().equals(user.getId()));
        if (alreadyJoined) {
            throw new RuntimeException("User is already participating in this challenge.");
        }

        // -------- cooldown check --------
        // Approximate the user's most recent participation by the largest
        // startTime among the challenges they are already enrolled in (ignoring future
        // ones).
        // NOTE: this can be highly inefficient for many challenges!
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime cooldownThreshold = now.minusSeconds(PARTICIPATION_COOLDOWN_SECONDS);
        LocalDateTime lastParticipation = repo.findAll().stream()
                .filter(c -> c.getUsers() != null && c.getUsers().stream()
                        .anyMatch(p -> p.getId() != null && p.getId().equals(user.getId())))
                .map(Challenge::getStartTime)
                .filter(t -> t != null && !t.isAfter(now))
                .max(Comparator.naturalOrder())
                .orElse(null);
        if (lastParticipation != null && lastParticipation.isAfter(cooldownThreshold)) {
            throw new RuntimeException(
                    "User must wait at least " + PARTICIPATION_COOLDOWN_SECONDS
                            + " seconds between challenges.");
        }

        // -------- verification code check (trim + lower-case both sides) --------
        String providedCode = verificationCode.trim().toLowerCase();
        String expectedCode = challenge.getVerificationCode() == null
                ? ""
                : challenge.getVerificationCode().trim().toLowerCase();
        if (!providedCode.equals(expectedCode)) {
            throw new RuntimeException("Invalid verification code.");
        }

        // -------- mutate state and persist --------
        participants.add(user);
        challenge.setUsers(participants);
        // NOTE: the currency is not yet distributed but just given out fully to all
        // users! This could be made better, e.g. with dividing through the maxPlayers
        // (must not be 0!)
        user.setCurrency(user.getCurrency() + challenge.getCurrency());
        user.setExperiencePoints(user.getExperiencePoints() + challenge.getExperiencePoints());

        repo.save(challenge);
        userRepo.save(user);

        return challenge.getId();
    }

}