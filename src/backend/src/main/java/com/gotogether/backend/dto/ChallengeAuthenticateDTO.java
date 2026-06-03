package com.gotogether.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Request payload for authenticating a challenge operation.
 *
 * <p>
 * The authentication is authorized by the hosting company's credentials. The
 * {@code challengeId} is supplied separately as a path variable on the
 * controller side.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ChallengeAuthenticateDTO {

    /** Email of the hosting company (required, used for authentication). */
    private String companyEmail;

    /** Password of the hosting company (required, used for authentication). */
    private String companyPassword;
}
