package com.alexandracoder.littleneighbors.message.dto;

public record SendMessageDTO(
        Long matchId,
        Long senderId,
        String content
) {
}
