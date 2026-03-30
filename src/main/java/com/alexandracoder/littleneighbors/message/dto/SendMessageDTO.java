package com.alexandracoder.littleneighbors.message.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SendMessageDTO(
        @NotNull Long matchId,
        @NotBlank String content
) {
}
