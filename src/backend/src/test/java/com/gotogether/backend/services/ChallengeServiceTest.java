package com.gotogether.backend.services;

import com.gotogether.backend.dto.ChallengeDTO;
import com.gotogether.backend.dto.ChallengeSortAttribute;
import com.gotogether.backend.dto.ChallengeVerificationDTO;
import com.gotogether.backend.mapper.ChallengeMapper;
import com.gotogether.backend.model.Challenge;
import com.gotogether.backend.model.Company;
import com.gotogether.backend.model.Location;
import com.gotogether.backend.repository.ChallengeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
}
