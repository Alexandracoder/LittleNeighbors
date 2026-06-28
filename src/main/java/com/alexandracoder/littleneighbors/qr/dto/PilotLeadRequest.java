package com.alexandracoder.littleneighbors.qr.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PilotLeadRequest {

    @NotBlank(message = "Email is required")
    @Email(message = "Email format is invalid")
    private String email;

    @NotBlank(message = "Neighborhood is required")
    private String neighborhood;

    @AssertTrue(message = "Debes aceptar la política de privacidad para continuar")
    private boolean consentGiven;

    @NotBlank(message = "Privacy policy version is required")
    private String privacyPolicyVersion;
}