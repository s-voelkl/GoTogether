package com.gotogether.backend.mapper;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.stereotype.Component;

import com.gotogether.backend.dto.ChallengeDTO;
import com.gotogether.backend.dto.ChallengeVerificationDTO;
import com.gotogether.backend.model.Challenge;

@Component
@RequiredArgsConstructor
public class ChallengeMapper {

    public ChallengeDTO toDTO(Challenge challenge) {
        return ChallengeDTO.builder()
                .id(challenge.getId())
                .title(challenge.getTitle())
                .description(challenge.getDescription())
                .isArchived(challenge.isArchived())
                .startTime(challenge.getStartTime())
                .location(challenge.getLocation())
                .durationMinutes(challenge.getDurationMinutes())
                .currency(challenge.getCurrency())
                .experiencePoints(challenge.getExperiencePoints())
                .minSocialBattery(challenge.getMinSocialBattery())
                // verificationCode: intentionally not included in the DTO, should not be
                // exposed to the client
                .maxPlayers(challenge.getMaxPlayers())
                .currentPlayers(challenge.getUsers().size())
                .hostCompanyName(challenge.getHost() != null ? challenge.getHost().getName() : "")
                .topicIds(challenge.getTopics() != null
                        ? challenge.getTopics().stream().map(t -> t.getId()).toList()
                        : List.of())
                .build();
    }

    public ChallengeVerificationDTO toVerificationDTO(Challenge challenge) {
        return ChallengeVerificationDTO.builder()
                .id(challenge.getId())
                .verificationCode(challenge.getVerificationCode())
                .build();
    }
}