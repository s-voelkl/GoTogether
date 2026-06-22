package com.gotogether.backend.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class ChallengeVerificationDTO {

    private UUID id;

    private String verificationCode; // 5 digits

}
