package com.gotogether.backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gotogether.backend.dto.ChallengeCreateDTO;
import com.gotogether.backend.dto.ChallengeAuthenticateDTO;
import com.gotogether.backend.dto.ChallengeFilterDTO;
import com.gotogether.backend.services.ChallengeService;

import java.util.UUID;

@RestController
@RequestMapping("/api/challenges")
public class ChallengeController {

    private final ChallengeService service;

    public ChallengeController(ChallengeService service) {
        this.service = service;
    }

    /**
     * Returns a single challenge by its id.
     *
     * @param id the challenge id (path variable)
     * @return {@code 200 OK} with the
     *         {@link com.gotogether.backend.dto.ChallengeDTO}
     *         on success, or {@code 404 NOT FOUND} with an error message when no
     *         challenge exists for the given id
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable UUID id) {
        try {
            return ResponseEntity.ok(service.getChallengeById(id));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }

    /**
     * Searches challenges using the supplied {@link ChallengeFilterDTO} and
     * returns the matching results.
     *
     * <p>
     * The filter is provided as a JSON request body. All filter fields are
     * optional <b>except</b> {@code latitude} and {@code longitude}, which are
     * required because the result set is always sorted by distance from the
     * given search center (as the final tiebreaker). Numeric and boolean
     * fields fall back to sensible defaults (0 / {@code true}) when omitted,
     * and {@code limit} falls back to the service default when not supplied.
     *
     * @param filter the filter, sort and paging parameters; {@code latitude}
     *               and {@code longitude} must be set and within their valid
     *               ranges ([-90, 90] and [-180, 180])
     * @return {@code 200 OK} with the matching
     *         {@link com.gotogether.backend.dto.ChallengeDTO} list, or
     *         {@code 500 INTERNAL SERVER ERROR} with an error message if the
     *         filter is invalid (e.g. missing or out-of-range lat/lon)
     */
    @PostMapping("/filter")
    public ResponseEntity<?> getChallengesByFilter(@RequestBody ChallengeFilterDTO filter) {
        try {
            return ResponseEntity.ok(service.getChallengesByFilter(
                    filter.getIdLike(),
                    filter.getTitleLike(),
                    filter.getDescriptionLike(),
                    filter.getIsArchived(),
                    filter.getStartTimeFrom(),
                    filter.getStartTimeTo(),
                    filter.getMinDurationMinutes() == null ? 0 : filter.getMinDurationMinutes(),
                    filter.getMaxDurationMinutes() == null ? 0 : filter.getMaxDurationMinutes(),
                    filter.getMinCurrencyReward() == null ? 0 : filter.getMinCurrencyReward(),
                    filter.getMinExperiencePointsReward() == null ? 0 : filter.getMinExperiencePointsReward(),
                    filter.getMaxSocialBattery() == null ? 0 : filter.getMaxSocialBattery(),
                    filter.getMaxPlayers() == null ? 0 : filter.getMaxPlayers(),
                    filter.getMaxCurrentPlayers() == null ? 0 : filter.getMaxCurrentPlayers(),
                    filter.getLatitude(),
                    filter.getLongitude(),
                    filter.getRadiusMeters() == null ? 0 : filter.getRadiusMeters(),
                    filter.getHostCompanyNameLike(),
                    filter.getTopicIds(),
                    filter.getSortBy1(),
                    filter.getSort1Ascending() == null ? true : filter.getSort1Ascending(),
                    filter.getSortBy2(),
                    filter.getSort2Ascending() == null ? true : filter.getSort2Ascending(),
                    filter.getLimit() == null ? 0 : filter.getLimit()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    /**
     * Creates a new challenge on behalf of an authenticated company.
     *
     * <p>
     * The company is authenticated via {@code companyEmail} and
     * {@code companyPassword} in the request body. On success the configured
     * currency reward is transferred from the company to the new challenge
     * and a {@link com.gotogether.backend.dto.ChallengeCreatedDTO} is
     * returned, containing the new id and the verification code.
     *
     * @param dto the challenge creation payload
     * @return {@code 201 CREATED} with the created challenge response, or
     *         {@code 400 BAD REQUEST} with an error message on validation or
     *         authentication errors
     */
    @PostMapping
    public ResponseEntity<?> createChallenge(@RequestBody ChallengeCreateDTO dto) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(service.createChallenge(
                    dto.getCompanyEmail(),
                    dto.getCompanyPassword(),
                    dto.getTitle(),
                    dto.getDescription(),
                    dto.getStartTime(),
                    dto.getDurationMinutes(),
                    dto.getLatitude(),
                    dto.getLongitude(),
                    dto.getCurrency(),
                    dto.getMinSocialBattery(),
                    dto.getMaxPlayers(),
                    dto.getTopicIds()));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    /**
     * Deletes a challenge and refunds its currency reward to the hosting
     * company (when the company still exists).
     *
     * @param id  the id of the challenge to delete (path variable)
     * @param dto the company credentials in the request body
     * @return {@code 200 OK} with the id of the deleted challenge, or
     *         {@code 400 BAD REQUEST} with an error message on authentication
     *         or lookup errors
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteChallenge(@PathVariable UUID id, @RequestBody ChallengeAuthenticateDTO dto) {
        try {
            return ResponseEntity.ok(service.deleteChallenge(
                    dto.getCompanyEmail(), dto.getCompanyPassword(), id));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

}
