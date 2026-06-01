package com.gotogether.backend.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class ChallengeFilterDTO {

    @Builder.Default
    private String idLike = "";

    @Builder.Default
    private String titleLike = "";

    @Builder.Default
    private String descriptionLike = "";

    @Builder.Default
    private Boolean isArchived = false;

    private LocalDateTime startTimeFrom;
    private LocalDateTime startTimeTo;

    @Builder.Default
    private Double minDurationMinutes = 0.0;

    @Builder.Default
    private Double maxDurationMinutes = 0.0;

    @Builder.Default
    private Integer minCurrencyReward = 0;

    @Builder.Default
    private Integer minExperiencePointsReward = 0;

    @Builder.Default
    private Integer maxSocialBattery = 0;

    @Builder.Default
    private Integer maxPlayers = 0;

    @Builder.Default
    private Integer maxCurrentPlayers = 0;

    private Double latitude;
    private Double longitude;

    @Builder.Default
    private Integer radiusMeters = 0;

    @Builder.Default
    private String hostCompanyNameLike = "";

    @Builder.Default
    private List<UUID> topicIds = new ArrayList<>();

    private ChallengeSortAttribute sortBy1;

    @Builder.Default
    private Boolean sort1Ascending = true;

    private ChallengeSortAttribute sortBy2;

    @Builder.Default
    private Boolean sort2Ascending = true;

    private Integer limit;

}
