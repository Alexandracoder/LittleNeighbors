package com.alexandracoder.littleneighbors.message.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SendMessageDTO(
        @NotNull(message = "Receiver ID is required")
        Long receiverId,

        Long matchId,

        @NotBlank(message = "Message content cannot be empty")
        String content
) {}