package com.alexandracoder.littleneighbors.message.dto;

import java.time.LocalDateTime;

public record MessageWebSocketDTO(
        Long id,
        Long matchId,
        Long senderId,
        String senderName,
        String content,
        LocalDateTime sentAt
) {}