package com.gotogether.backend.dto;

import com.gotogether.backend.model.Location;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class ChallengeDTO {

    private UUID id;

    private String title;

    private String description;

    @Builder.Default
    private boolean isArchived = false;

    private LocalDateTime startTime;

    private Location location;

    private int durationMinutes;

    private int currency;

    private int experiencePoints;

    @Builder.Default
    private int minSocialBattery = 0;

    // explicitely not included in the DTO, as it should not be exposed to the
    // client
    // private String verificationCode; // 5 digits

    @Builder.Default
    private int maxPlayers = 0; // 0 means no limit

    @Builder.Default
    private int currentPlayers = 0;

    @Builder.Default
    private String hostCompanyName = "";

    @Builder.Default
    private List<UUID> topicIds = List.of();

}
