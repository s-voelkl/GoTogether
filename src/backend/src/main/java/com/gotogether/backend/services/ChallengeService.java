package com.gotogether.backend.services;

import com.gotogether.backend.dto.ChallengeDTO;
import com.gotogether.backend.dto.ChallengeSortAttribute;
import com.gotogether.backend.dto.ChallengeVerificationDTO;
import com.gotogether.backend.mapper.ChallengeMapper;
import com.gotogether.backend.model.Challenge;
import com.gotogether.backend.repository.ChallengeRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChallengeService {

    private final ChallengeRepository repo;

    private final ChallengeMapper challengeMapper;

    /** Mean Earth radius in kilometers used by the Haversine formula. */
    private static final int EARTH_RADIUS_KM = 6371;

    /** Hard cap on results returned by {@link #getChallengesByFilter}. */
    private static final int MAX_RESULT_LIMIT = 100;

    /** Default page size when the caller does not specify a limit. */
    private static final int DEFAULT_RESULT_LIMIT = 10;

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

}