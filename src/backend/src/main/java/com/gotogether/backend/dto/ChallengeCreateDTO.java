package com.gotogether.backend.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Request payload for creating a new challenge.
 *
 * <p>
 * The challenge is always created on behalf of a company; the company
 * authenticates with {@link #companyEmail} and {@link #companyPassword}.
 * All other fields except {@link #title}, {@link #startTime} and
 * {@link #topicIds} are optional and fall back to service-side defaults.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChallengeCreateDTO {

    /** Email of the hosting company (required, used for authentication). */
    private String companyEmail;

    /** Password of the hosting company (required, used for authentication). */
    private String companyPassword;

    /** Challenge title (required). */
    private String title;

    /** Optional description; defaults to {@link #title} when blank. */
    private String description;

    /** Required start time. */
    private LocalDateTime startTime;

    /** Optional duration in minutes; defaults to 120 when {@code null}. */
    private Integer durationMinutes;

    /** Optional latitude; falls back to the host company's location. */
    private Double latitude;

    /** Optional longitude; falls back to the host company's location. */
    private Double longitude;

    /** Optional currency reward funded by the company; defaults to 100. */
    private Integer currency;

    /** Optional minimum required social battery; defaults to 0. */
    private Integer minSocialBattery;

    /** Optional player cap; defaults to 0 (unlimited). */
    private Integer maxPlayers;

    /** Required list of topic ids associated with the challenge. */
    private List<UUID> topicIds;
}
