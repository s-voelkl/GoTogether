package com.gotogether.backend.dto;

import java.util.UUID;

import lombok.Builder;
import lombok.Data;

/**
 * Response payload returned after successfully creating a challenge.
 *
 * <p>
 * Contains the newly assigned challenge id, the verification code the host
 * uses to confirm participation, and the QR code encoding that verification
 * code as a PNG image (Base64-encoded so the binary payload can travel in a
 * standard JSON response and be embedded directly via a {@code data:} URL).
 */
@Data
@Builder
public class ChallengeCreatedDTO {

    /** Id of the newly created challenge. */
    private UUID id;

    /** Five-character alphanumeric verification code. */
    private String verificationCode;

    /** Base64-encoded PNG image encoding the verification code as a QR code. */
    private String qrCodePngBase64;
}
