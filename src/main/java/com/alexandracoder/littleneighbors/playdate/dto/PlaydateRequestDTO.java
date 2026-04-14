package com.alexandracoder.littleneighbors.playdate.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;

public record PlaydateRequestDTO(
        String title,

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        LocalDateTime startTime,

        String description,
        Long matchId
) {}