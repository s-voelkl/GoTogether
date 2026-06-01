package com.gotogether.backend.dto;

import java.util.UUID;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Request payload describing a user's wish to participate in a challenge.
 *
 * <p>
 * The user authenticates with {@link #userEmail} and {@link #userPassword}.
 * The supplied coordinates ({@link #userLatitude}, {@link #userLongitude})
 * are used to verify that the user is physically near the challenge, and
 * {@link #verificationCode} must match the challenge's verification code.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChallengeParticipanceDTO {

    /** Email of the participating user (required, used for authentication). */
    private String userEmail;

    /** Password of the participating user (required, used for authentication). */
    private String userPassword;

    /** Current latitude of the user in decimal degrees ([-90, 90]). */
    private Double userLatitude;

    /** Current longitude of the user in decimal degrees ([-180, 180]). */
    private Double userLongitude;

    /** Id of the challenge the user wants to join. */
    private UUID challengeId;

    /** Verification code shown at the challenge location. */
    private String verificationCode;
}
