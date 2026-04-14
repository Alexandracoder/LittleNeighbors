package com.alexandracoder.littleneighbors.playdate.dto;

import java.time.LocalDateTime;

public record PlaydateResponseDTO(
        Long id,
        String title,
        String description,
        LocalDateTime startTime,
        String status,
        Long matchId,
        String requesterFamilyName,
        String receiverFamilyName
) {}