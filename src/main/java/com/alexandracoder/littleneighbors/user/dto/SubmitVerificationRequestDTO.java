package com.alexandracoder.littleneighbors.user.dto;

import jakarta.validation.constraints.NotBlank;

public record SubmitVerificationRequestDTO(
        @NotBlank String idDocumentUrl,
        @NotBlank String selfieUrl
) {
}
