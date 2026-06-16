package com.alexandracoder.littleneighbors.message.dto;

import java.time.LocalDateTime;

public record MessageResponseDTO(
        Long id,
        Long senderId,
        String senderEmail,
        Long receiverId,
        Long matchId,
        String content,
        LocalDateTime sentAt
) {}