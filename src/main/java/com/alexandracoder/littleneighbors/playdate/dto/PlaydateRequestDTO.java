package com.alexandracoder.littleneighbors.playdate.dto;

import java.time.LocalDateTime;

public record PlaydateRequestDTO(
        String title,
        LocalDateTime startTime,
        String description,
        Long matchId
) {}