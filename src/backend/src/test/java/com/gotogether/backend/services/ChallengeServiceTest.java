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
import com.gotogether.backend.repository.ChallengeRepository;
import com.gotogether.backend.repository.CompanyRepository;
import com.gotogether.backend.repository.TopicRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChallengeServiceTest {

    @Mock
    private ChallengeRepository challengeRepository;

    @Mock
    private CompanyRepository companyRepository;

    @Mock
    private TopicRepository topicRepository;

    @Spy
    private ChallengeMapper challengeMapper;

    @InjectMocks
    private ChallengeService challengeService;

    // Berlin center, used as the search center for filter tests.
    private static final double BERLIN_LAT = 52.52;
    private static final double BERLIN_LON = 13.405;

    @BeforeEach
    void setUp() {
        challengeMapper = new ChallengeMapper();
    }

    private Company buildCompany(String name) {
        Company company = new Company();
        company.setId(UUID.randomUUID());
        company.setName(name);
        return company;
    }

    private Challenge buildChallenge(String title, double lat, double lon) {
        Challenge c = new Challenge();
        c.setId(UUID.randomUUID());
        c.setTitle(title);
        c.setDescription("desc " + title);
        c.setArchived(false);
        c.setStartTime(LocalDateTime.of(2026, 6, 1, 10, 0));
        c.setLocation(new Location(lat, lon));
        c.setDurationMinutes(60);
        c.setCurrency(100);
        c.setExperiencePoints(50);
        c.setMinSocialBattery(20);
        c.setVerificationCode("12345");
        c.setMaxPlayers(10);
        c.setHost(buildCompany("Acme Corp"));
        c.setTopics(new ArrayList<>());
        c.setUsers(new ArrayList<>());
        return c;
    }

    // -------------------- getChallengeById --------------------

    @Test
    void getChallengeById_ChallengeExists_ReturnsDTO() {
        Challenge challenge = buildChallenge("Yoga", BERLIN_LAT, BERLIN_LON);
        when(challengeRepository.findById(challenge.getId())).thenReturn(Optional.of(challenge));

        ChallengeDTO result = challengeService.getChallengeById(challenge.getId());

        assertNotNull(result);
        assertEquals(challenge.getId(), result.getId());
        assertEquals("Yoga", result.getTitle());
        verify(challengeRepository, times(1)).findById(challenge.getId());
    }

    @Test
    void getChallengeById_ChallengeDoesNotExist_ThrowsRuntimeException() {
        UUID id = UUID.randomUUID();
        when(challengeRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> challengeService.getChallengeById(id));
        verify(challengeRepository, times(1)).findById(id);
    }

    // -------------------- verifyChallenge --------------------

    @Test
    void verifyChallenge_ValidCode_ReturnsVerificationDTO() {
        Challenge challenge = buildChallenge("Run", BERLIN_LAT, BERLIN_LON);
        challenge.setVerificationCode("54321");
        when(challengeRepository.findById(challenge.getId())).thenReturn(Optional.of(challenge));

        ChallengeVerificationDTO result = challengeService.verifyChallenge(challenge.getId(), "54321");

        assertNotNull(result);
        assertEquals(challenge.getId(), result.getId());
        assertEquals("54321", result.getVerificationCode());
    }

    @Test
    void verifyChallenge_InvalidCode_ThrowsRuntimeException() {
        Challenge challenge = buildChallenge("Run", BERLIN_LAT, BERLIN_LON);
        challenge.setVerificationCode("54321");
        when(challengeRepository.findById(challenge.getId())).thenReturn(Optional.of(challenge));

        assertThrows(RuntimeException.class,
                () -> challengeService.verifyChallenge(challenge.getId(), "00000"));
    }

    @Test
    void verifyChallenge_ChallengeNotFound_ThrowsRuntimeException() {
        UUID id = UUID.randomUUID();
        when(challengeRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> challengeService.verifyChallenge(id, "12345"));
    }

    // -------------------- getChallengesByFilter: validation --------------------

    @Test
    void getChallengesByFilter_NullLatitude_ThrowsRuntimeException() {
        assertThrows(RuntimeException.class, () -> challengeService.getChallengesByFilter(
                "", "", "", false, null, null, 0, 0, 0, 0, 0, 0, 0,
                null, BERLIN_LON, 0, "", List.of(),
                null, true, null, true, 0));
    }

    @Test
    void getChallengesByFilter_NullLongitude_ThrowsRuntimeException() {
        assertThrows(RuntimeException.class, () -> challengeService.getChallengesByFilter(
                "", "", "", false, null, null, 0, 0, 0, 0, 0, 0, 0,
                BERLIN_LAT, null, 0, "", List.of(),
                null, true, null, true, 0));
    }

    @Test
    void getChallengesByFilter_LatitudeOutOfRange_ThrowsRuntimeException() {
        assertThrows(RuntimeException.class, () -> challengeService.getChallengesByFilter(
                "", "", "", false, null, null, 0, 0, 0, 0, 0, 0, 0,
                95.0, BERLIN_LON, 0, "", List.of(),
                null, true, null, true, 0));
    }

    @Test
    void getChallengesByFilter_LongitudeOutOfRange_ThrowsRuntimeException() {
        assertThrows(RuntimeException.class, () -> challengeService.getChallengesByFilter(
                "", "", "", false, null, null, 0, 0, 0, 0, 0, 0, 0,
                BERLIN_LAT, 200.0, 0, "", List.of(),
                null, true, null, true, 0));
    }

    // -------------------- getChallengesByFilter: filtering --------------------

    @Test
    void getChallengesByFilter_TitleLike_ReturnsMatchingChallenges() {
        Challenge yoga = buildChallenge("Morning Yoga", BERLIN_LAT, BERLIN_LON);
        Challenge run = buildChallenge("Evening Run", BERLIN_LAT, BERLIN_LON);
        when(challengeRepository.findAll()).thenReturn(List.of(yoga, run));

        List<ChallengeDTO> result = challengeService.getChallengesByFilter(
                "", "yoga", "", false, null, null, 0, 0, 0, 0, 0, 0, 0,
                BERLIN_LAT, BERLIN_LON, 0, "", List.of(),
                null, true, null, true, 0);

        assertEquals(1, result.size());
        assertEquals("Morning Yoga", result.get(0).getTitle());
    }

    @Test
    void getChallengesByFilter_ArchivedFlag_FiltersByArchived() {
        Challenge active = buildChallenge("Active", BERLIN_LAT, BERLIN_LON);
        Challenge archived = buildChallenge("Archived", BERLIN_LAT, BERLIN_LON);
        archived.setArchived(true);
        when(challengeRepository.findAll()).thenReturn(List.of(active, archived));

        List<ChallengeDTO> result = challengeService.getChallengesByFilter(
                "", "", "", true, null, null, 0, 0, 0, 0, 0, 0, 0,
                BERLIN_LAT, BERLIN_LON, 0, "", List.of(),
                null, true, null, true, 0);

        assertEquals(1, result.size());
        assertEquals("Archived", result.get(0).getTitle());
    }

    @Test
    void getChallengesByFilter_MinCurrencyReward_FiltersByCurrency() {
        Challenge cheap = buildChallenge("Cheap", BERLIN_LAT, BERLIN_LON);
        cheap.setCurrency(10);
        Challenge rich = buildChallenge("Rich", BERLIN_LAT, BERLIN_LON);
        rich.setCurrency(500);
        when(challengeRepository.findAll()).thenReturn(List.of(cheap, rich));

        List<ChallengeDTO> result = challengeService.getChallengesByFilter(
                "", "", "", false, null, null, 0, 0, 100, 0, 0, 0, 0,
                BERLIN_LAT, BERLIN_LON, 0, "", List.of(),
                null, true, null, true, 0);

        assertEquals(1, result.size());
        assertEquals("Rich", result.get(0).getTitle());
    }

    @Test
    void getChallengesByFilter_MaxSocialBattery_FiltersByRequiredSocialBattery() {
        Challenge low = buildChallenge("Low", BERLIN_LAT, BERLIN_LON);
        low.setMinSocialBattery(10);
        Challenge high = buildChallenge("High", BERLIN_LAT, BERLIN_LON);
        high.setMinSocialBattery(80);
        when(challengeRepository.findAll()).thenReturn(List.of(low, high));

        List<ChallengeDTO> result = challengeService.getChallengesByFilter(
                "", "", "", false, null, null, 0, 0, 0, 0, 50, 0, 0,
                BERLIN_LAT, BERLIN_LON, 0, "", List.of(),
                null, true, null, true, 0);

        assertEquals(1, result.size());
        assertEquals("Low", result.get(0).getTitle());
    }

    @Test
    void getChallengesByFilter_RadiusMeters_FiltersByDistance() {
        Challenge near = buildChallenge("Near", BERLIN_LAT, BERLIN_LON);
        // ~111 km north of Berlin
        Challenge far = buildChallenge("Far", BERLIN_LAT + 1.0, BERLIN_LON);
        when(challengeRepository.findAll()).thenReturn(List.of(near, far));

        List<ChallengeDTO> result = challengeService.getChallengesByFilter(
                "", "", "", false, null, null, 0, 0, 0, 0, 0, 0, 0,
                BERLIN_LAT, BERLIN_LON, 5000, "", List.of(),
                null, true, null, true, 0);

        assertEquals(1, result.size());
        assertEquals("Near", result.get(0).getTitle());
    }

    // -------------------- getChallengesByFilter: sorting --------------------

    @Test
    void getChallengesByFilter_SortByDurationAscending_SortsCorrectly() {
        Challenge longC = buildChallenge("Long", BERLIN_LAT, BERLIN_LON);
        longC.setDurationMinutes(120);
        Challenge shortC = buildChallenge("Short", BERLIN_LAT, BERLIN_LON);
        shortC.setDurationMinutes(30);
        Challenge mediumC = buildChallenge("Medium", BERLIN_LAT, BERLIN_LON);
        mediumC.setDurationMinutes(60);
        when(challengeRepository.findAll()).thenReturn(List.of(longC, shortC, mediumC));

        List<ChallengeDTO> result = challengeService.getChallengesByFilter(
                "", "", "", false, null, null, 0, 0, 0, 0, 0, 0, 0,
                BERLIN_LAT, BERLIN_LON, 0, "", List.of(),
                ChallengeSortAttribute.DURATION, true, null, true, 0);

        assertEquals(3, result.size());
        assertEquals("Short", result.get(0).getTitle());
        assertEquals("Medium", result.get(1).getTitle());
        assertEquals("Long", result.get(2).getTitle());
    }

    @Test
    void getChallengesByFilter_SortByCurrencyRewardDescending_SortsCorrectly() {
        Challenge a = buildChallenge("A", BERLIN_LAT, BERLIN_LON);
        a.setCurrency(50);
        Challenge b = buildChallenge("B", BERLIN_LAT, BERLIN_LON);
        b.setCurrency(300);
        Challenge c = buildChallenge("C", BERLIN_LAT, BERLIN_LON);
        c.setCurrency(150);
        when(challengeRepository.findAll()).thenReturn(List.of(a, b, c));

        List<ChallengeDTO> result = challengeService.getChallengesByFilter(
                "", "", "", false, null, null, 0, 0, 0, 0, 0, 0, 0,
                BERLIN_LAT, BERLIN_LON, 0, "", List.of(),
                ChallengeSortAttribute.CURRENCY_REWARD, false, null, true, 0);

        assertEquals(3, result.size());
        assertEquals("B", result.get(0).getTitle());
        assertEquals("C", result.get(1).getTitle());
        assertEquals("A", result.get(2).getTitle());
    }

    @Test
    void getChallengesByFilter_SortByDistanceAscending_SortsByDistanceFromCenter() {
        Challenge near = buildChallenge("Near", BERLIN_LAT, BERLIN_LON);
        Challenge mid = buildChallenge("Mid", BERLIN_LAT + 0.1, BERLIN_LON);
        Challenge far = buildChallenge("Far", BERLIN_LAT + 1.0, BERLIN_LON);
        when(challengeRepository.findAll()).thenReturn(List.of(far, near, mid));

        List<ChallengeDTO> result = challengeService.getChallengesByFilter(
                "", "", "", false, null, null, 0, 0, 0, 0, 0, 0, 0,
                BERLIN_LAT, BERLIN_LON, 0, "", List.of(),
                ChallengeSortAttribute.DISTANCE, true, null, true, 0);

        assertEquals(3, result.size());
        assertEquals("Near", result.get(0).getTitle());
        assertEquals("Mid", result.get(1).getTitle());
        assertEquals("Far", result.get(2).getTitle());
    }

    @Test
    void getChallengesByFilter_SecondarySort_AppliedAsTiebreaker() {
        // Same currency, different duration; primary CURRENCY desc keeps them
        // grouped, secondary DURATION asc orders within the group.
        Challenge a = buildChallenge("A", BERLIN_LAT, BERLIN_LON);
        a.setCurrency(100);
        a.setDurationMinutes(90);
        Challenge b = buildChallenge("B", BERLIN_LAT, BERLIN_LON);
        b.setCurrency(100);
        b.setDurationMinutes(30);
        Challenge c = buildChallenge("C", BERLIN_LAT, BERLIN_LON);
        c.setCurrency(100);
        c.setDurationMinutes(60);
        when(challengeRepository.findAll()).thenReturn(List.of(a, b, c));

        List<ChallengeDTO> result = challengeService.getChallengesByFilter(
                "", "", "", false, null, null, 0, 0, 0, 0, 0, 0, 0,
                BERLIN_LAT, BERLIN_LON, 0, "", List.of(),
                ChallengeSortAttribute.CURRENCY_REWARD, false,
                ChallengeSortAttribute.DURATION, true, 0);

        assertEquals(3, result.size());
        assertEquals("B", result.get(0).getTitle());
        assertEquals("C", result.get(1).getTitle());
        assertEquals("A", result.get(2).getTitle());
    }

    @Test
    void getChallengesByFilter_NoSortGiven_DefaultsToDistanceAscending() {
        Challenge near = buildChallenge("Near", BERLIN_LAT, BERLIN_LON);
        Challenge far = buildChallenge("Far", BERLIN_LAT + 1.0, BERLIN_LON);
        when(challengeRepository.findAll()).thenReturn(List.of(far, near));

        List<ChallengeDTO> result = challengeService.getChallengesByFilter(
                "", "", "", false, null, null, 0, 0, 0, 0, 0, 0, 0,
                BERLIN_LAT, BERLIN_LON, 0, "", List.of(),
                null, true, null, true, 0);

        assertEquals(2, result.size());
        assertEquals("Near", result.get(0).getTitle());
        assertEquals("Far", result.get(1).getTitle());
    }

    // -------------------- getChallengesByFilter: limiting --------------------

    @Test
    void getChallengesByFilter_ExplicitLimit_LimitsResultSize() {
        List<Challenge> many = new ArrayList<>();
        for (int i = 0; i < 20; i++) {
            many.add(buildChallenge("C" + i, BERLIN_LAT, BERLIN_LON));
        }
        when(challengeRepository.findAll()).thenReturn(many);

        List<ChallengeDTO> result = challengeService.getChallengesByFilter(
                "", "", "", false, null, null, 0, 0, 0, 0, 0, 0, 0,
                BERLIN_LAT, BERLIN_LON, 0, "", List.of(),
                null, true, null, true, 5);

        assertEquals(5, result.size());
    }

    @Test
    void getChallengesByFilter_ZeroLimit_AppliesDefaultLimit() {
        List<Challenge> many = new ArrayList<>();
        for (int i = 0; i < 25; i++) {
            many.add(buildChallenge("C" + i, BERLIN_LAT, BERLIN_LON));
        }
        when(challengeRepository.findAll()).thenReturn(many);

        List<ChallengeDTO> result = challengeService.getChallengesByFilter(
                "", "", "", false, null, null, 0, 0, 0, 0, 0, 0, 0,
                BERLIN_LAT, BERLIN_LON, 0, "", List.of(),
                null, true, null, true, 0);

        // DEFAULT_RESULT_LIMIT = 10
        assertEquals(10, result.size());
    }

    @Test
    void getChallengesByFilter_LimitAboveCap_CapsAtMaxResultLimit() {
        List<Challenge> many = new ArrayList<>();
        for (int i = 0; i < 150; i++) {
            many.add(buildChallenge("C" + i, BERLIN_LAT, BERLIN_LON));
        }
        when(challengeRepository.findAll()).thenReturn(many);

        List<ChallengeDTO> result = challengeService.getChallengesByFilter(
                "", "", "", false, null, null, 0, 0, 0, 0, 0, 0, 0,
                BERLIN_LAT, BERLIN_LON, 0, "", List.of(),
                null, true, null, true, 500);

        // MAX_RESULT_LIMIT = 100
        assertEquals(100, result.size());
    }

    // -------------------- createChallenge --------------------

    private Company buildAuthCompany(String email, String password, int currency) {
        Company company = new Company();
        company.setId(UUID.randomUUID());
        company.setName("HostCo");
        company.setEmail(email);
        company.setPassword(password);
        company.setCurrency(currency);
        company.setLocation(new Location(BERLIN_LAT, BERLIN_LON));
        return company;
    }

    private Topic buildTopic(String name) {
        Topic t = new Topic();
        t.setId(UUID.randomUUID());
        t.setName(name);
        return t;
    }

    @Test
    void createChallenge_ValidInputs_PersistsChallengeAndReturnsDTO() {
        Company company = buildAuthCompany("host@example.com", "pw", 500);
        Topic topic = buildTopic("Wandern");
        when(companyRepository.findByEmail("host@example.com")).thenReturn(company);
        when(topicRepository.findById(topic.getId())).thenReturn(Optional.of(topic));
        when(challengeRepository.save(any(Challenge.class))).thenAnswer(inv -> {
            Challenge c = inv.getArgument(0);
            c.setId(UUID.randomUUID());
            return c;
        });

        ChallengeCreatedDTO result = challengeService.createChallenge(
                "host@example.com", "pw",
                "Trail Run", "Sunday morning run",
                LocalDateTime.of(2026, 7, 1, 9, 0),
                90, 52.6, 13.5, 200, 30, 8,
                List.of(topic.getId()));

        assertNotNull(result);
        assertNotNull(result.getId());
        assertNotNull(result.getVerificationCode());
        assertEquals(5, result.getVerificationCode().length());
        assertNotNull(result.getQrCodePngBase64());
        // PNG magic bytes "\x89PNG" -> base64 prefix "iVBORw0KGgo"
        assertTrue(result.getQrCodePngBase64().startsWith("iVBORw0KGgo"),
                "QR code output should be a Base64-encoded PNG");

        ArgumentCaptor<Challenge> captor = ArgumentCaptor.forClass(Challenge.class);
        verify(challengeRepository).save(captor.capture());
        Challenge saved = captor.getValue();
        assertEquals("Trail Run", saved.getTitle());
        assertEquals("Sunday morning run", saved.getDescription());
        assertEquals(90, saved.getDurationMinutes());
        assertEquals(200, saved.getCurrency());
        assertEquals(100, saved.getExperiencePoints()); // static placeholder
        assertEquals(30, saved.getMinSocialBattery());
        assertEquals(8, saved.getMaxPlayers());
        assertEquals(52.6, saved.getLocation().getLatitude());
        assertEquals(13.5, saved.getLocation().getLongitude());
        assertEquals(company, saved.getHost());
        assertEquals(1, saved.getTopics().size());

        // Currency was withdrawn from the company.
        assertEquals(300, company.getCurrency());
        verify(companyRepository).save(company);
    }

    @Test
    void createChallenge_BlankDescription_DefaultsToTitle() {
        Company company = buildAuthCompany("host@example.com", "pw", 500);
        Topic topic = buildTopic("Kunst");
        when(companyRepository.findByEmail("host@example.com")).thenReturn(company);
        when(topicRepository.findById(topic.getId())).thenReturn(Optional.of(topic));
        when(challengeRepository.save(any(Challenge.class))).thenAnswer(inv -> inv.getArgument(0));

        challengeService.createChallenge(
                "host@example.com", "pw",
                "Picnic", "   ",
                LocalDateTime.of(2026, 7, 1, 9, 0),
                null, null, null, null, null, null,
                List.of(topic.getId()));

        ArgumentCaptor<Challenge> captor = ArgumentCaptor.forClass(Challenge.class);
        verify(challengeRepository).save(captor.capture());
        assertEquals("Picnic", captor.getValue().getDescription());
    }

    @Test
    void createChallenge_OmittedOptionalFields_UsesDefaults() {
        Company company = buildAuthCompany("host@example.com", "pw", 500);
        Topic topic = buildTopic("Lesen");
        when(companyRepository.findByEmail("host@example.com")).thenReturn(company);
        when(topicRepository.findById(topic.getId())).thenReturn(Optional.of(topic));
        when(challengeRepository.save(any(Challenge.class))).thenAnswer(inv -> inv.getArgument(0));

        challengeService.createChallenge(
                "host@example.com", "pw",
                "Bookclub", null,
                LocalDateTime.of(2026, 7, 1, 9, 0),
                null, null, null, null, null, null,
                List.of(topic.getId()));

        ArgumentCaptor<Challenge> captor = ArgumentCaptor.forClass(Challenge.class);
        verify(challengeRepository).save(captor.capture());
        Challenge saved = captor.getValue();
        assertEquals(120, saved.getDurationMinutes());
        assertEquals(100, saved.getCurrency());
        assertEquals(0, saved.getMinSocialBattery());
        assertEquals(0, saved.getMaxPlayers());
        // location falls back to company location
        assertEquals(BERLIN_LAT, saved.getLocation().getLatitude());
        assertEquals(BERLIN_LON, saved.getLocation().getLongitude());
        // company funded the default 100 reward
        assertEquals(400, company.getCurrency());
    }

    @Test
    void createChallenge_InvalidCredentials_ThrowsRuntimeException() {
        Company company = buildAuthCompany("host@example.com", "pw", 500);
        when(companyRepository.findByEmail("host@example.com")).thenReturn(company);

        assertThrows(RuntimeException.class, () -> challengeService.createChallenge(
                "host@example.com", "wrong",
                "T", "D", LocalDateTime.now(), null, null, null, null, null, null,
                List.of(UUID.randomUUID())));
        verify(challengeRepository, never()).save(any());
    }

    @Test
    void createChallenge_UnknownCompany_ThrowsRuntimeException() {
        when(companyRepository.findByEmail("nobody@example.com")).thenReturn(null);

        assertThrows(RuntimeException.class, () -> challengeService.createChallenge(
                "nobody@example.com", "pw",
                "T", "D", LocalDateTime.now(), null, null, null, null, null, null,
                List.of(UUID.randomUUID())));
    }

    @Test
    void createChallenge_MissingTitle_ThrowsRuntimeException() {
        Company company = buildAuthCompany("host@example.com", "pw", 500);
        when(companyRepository.findByEmail("host@example.com")).thenReturn(company);

        assertThrows(RuntimeException.class, () -> challengeService.createChallenge(
                "host@example.com", "pw",
                " ", "D", LocalDateTime.now(), null, null, null, null, null, null,
                List.of(UUID.randomUUID())));
    }

    @Test
    void createChallenge_MissingTopics_ThrowsRuntimeException() {
        Company company = buildAuthCompany("host@example.com", "pw", 500);
        when(companyRepository.findByEmail("host@example.com")).thenReturn(company);

        assertThrows(RuntimeException.class, () -> challengeService.createChallenge(
                "host@example.com", "pw",
                "T", "D", LocalDateTime.now(), null, null, null, null, null, null,
                List.of()));
    }

    @Test
    void createChallenge_InsufficientCompanyCurrency_ThrowsRuntimeException() {
        Company company = buildAuthCompany("host@example.com", "pw", 50);
        Topic topic = buildTopic("Kochen");
        when(companyRepository.findByEmail("host@example.com")).thenReturn(company);
        when(topicRepository.findById(topic.getId())).thenReturn(Optional.of(topic));

        assertThrows(RuntimeException.class, () -> challengeService.createChallenge(
                "host@example.com", "pw",
                "T", "D", LocalDateTime.now(), null, null, null, 200, null, null,
                List.of(topic.getId())));
        verify(challengeRepository, never()).save(any());
        assertEquals(50, company.getCurrency());
    }

    @Test
    void createChallenge_NegativeCurrency_ThrowsRuntimeException() {
        Company company = buildAuthCompany("host@example.com", "pw", 500);
        when(companyRepository.findByEmail("host@example.com")).thenReturn(company);

        assertThrows(RuntimeException.class, () -> challengeService.createChallenge(
                "host@example.com", "pw",
                "T", "D", LocalDateTime.now(), null, null, null, -10, null, null,
                List.of(UUID.randomUUID())));
    }

    @Test
    void createChallenge_UnknownTopic_ThrowsRuntimeException() {
        Company company = buildAuthCompany("host@example.com", "pw", 500);
        UUID missingTopicId = UUID.randomUUID();
        when(companyRepository.findByEmail("host@example.com")).thenReturn(company);
        when(topicRepository.findById(missingTopicId)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> challengeService.createChallenge(
                "host@example.com", "pw",
                "T", "D", LocalDateTime.now(), null, null, null, null, null, null,
                List.of(missingTopicId)));
    }

    // -------------------- deleteChallenge --------------------

    @Test
    void deleteChallenge_ValidRequest_DeletesAndRefundsCompany() {
        Company company = buildAuthCompany("host@example.com", "pw", 200);
        Challenge challenge = buildChallenge("ToDelete", BERLIN_LAT, BERLIN_LON);
        challenge.setCurrency(150);
        challenge.setHost(company);
        when(companyRepository.findByEmail("host@example.com")).thenReturn(company);
        when(challengeRepository.findById(challenge.getId())).thenReturn(Optional.of(challenge));
        when(companyRepository.findById(company.getId())).thenReturn(Optional.of(company));

        UUID returned = challengeService.deleteChallenge(
                "host@example.com", "pw", challenge.getId());

        assertEquals(challenge.getId(), returned);
        assertEquals(350, company.getCurrency()); // 200 + 150 refund
        verify(companyRepository).save(company);
        verify(challengeRepository).delete(challenge);
    }

    @Test
    void deleteChallenge_CompanyMissingInDb_StillDeletesWithoutRefund() {
        Company company = buildAuthCompany("host@example.com", "pw", 200);
        Challenge challenge = buildChallenge("ToDelete", BERLIN_LAT, BERLIN_LON);
        challenge.setCurrency(150);
        challenge.setHost(company);
        when(companyRepository.findByEmail("host@example.com")).thenReturn(company);
        when(challengeRepository.findById(challenge.getId())).thenReturn(Optional.of(challenge));
        // company removed between auth and refund
        when(companyRepository.findById(company.getId())).thenReturn(Optional.empty());

        UUID returned = challengeService.deleteChallenge(
                "host@example.com", "pw", challenge.getId());

        assertEquals(challenge.getId(), returned);
        verify(companyRepository, never()).save(any());
        verify(challengeRepository).delete(challenge);
    }

    @Test
    void deleteChallenge_WrongCompany_ThrowsRuntimeException() {
        Company authCompany = buildAuthCompany("host@example.com", "pw", 200);
        Company otherHost = buildAuthCompany("other@example.com", "pw", 0);
        Challenge challenge = buildChallenge("Other", BERLIN_LAT, BERLIN_LON);
        challenge.setHost(otherHost);
        when(companyRepository.findByEmail("host@example.com")).thenReturn(authCompany);
        when(challengeRepository.findById(challenge.getId())).thenReturn(Optional.of(challenge));

        assertThrows(RuntimeException.class, () -> challengeService.deleteChallenge(
                "host@example.com", "pw", challenge.getId()));
        verify(challengeRepository, never()).delete(any());
    }

    @Test
    void deleteChallenge_InvalidCredentials_ThrowsRuntimeException() {
        Company company = buildAuthCompany("host@example.com", "pw", 200);
        when(companyRepository.findByEmail("host@example.com")).thenReturn(company);

        assertThrows(RuntimeException.class, () -> challengeService.deleteChallenge(
                "host@example.com", "wrong", UUID.randomUUID()));
        verify(challengeRepository, never()).delete(any());
    }

    @Test
    void deleteChallenge_ChallengeNotFound_ThrowsRuntimeException() {
        Company company = buildAuthCompany("host@example.com", "pw", 200);
        UUID missing = UUID.randomUUID();
        when(companyRepository.findByEmail("host@example.com")).thenReturn(company);
        when(challengeRepository.findById(missing)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> challengeService.deleteChallenge(
                "host@example.com", "pw", missing));
    }
}
