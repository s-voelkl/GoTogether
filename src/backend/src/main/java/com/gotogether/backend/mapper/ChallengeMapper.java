package com.gotogether.backend.mapper;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.stereotype.Component;

import com.gotogether.backend.dto.ChallengeDTO;
import com.gotogether.backend.dto.ChallengeVerificationDTO;
import com.gotogether.backend.model.Challenge;

/**
 * Mapper class for converting {@link Challenge} entities to Data Transfer Objects (DTOs).
 * <p>
 * This class provides mapping methods to create {@link ChallengeDTO} and 
 * {@link ChallengeVerificationDTO} objects from the internal domain model.
 */
@Component
@RequiredArgsConstructor
public class ChallengeMapper {

    /**
     * Converts a {@link Challenge} entity to a {@link ChallengeDTO}.
     * <p>
     * Note: The verification code is intentionally not included in the returned 
     * DTO to prevent exposure to the client.
     *
     * @param challenge the {@link Challenge} entity to map
     * @return a {@link ChallengeDTO} containing the public details of the challenge
     */
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

    /**
     * Converts a {@link Challenge} entity to a {@link ChallengeVerificationDTO}.
     * <p>
     * This DTO includes the verification code associated with the challenge.
     *
     * @param challenge the {@link Challenge} entity to map
     * @return a {@link ChallengeVerificationDTO} containing the verification code for the challenge
     */
    public ChallengeVerificationDTO toVerificationDTO(Challenge challenge) {
        return ChallengeVerificationDTO.builder()
                .id(challenge.getId())
                .verificationCode(challenge.getVerificationCode())
                .build();
    }
}