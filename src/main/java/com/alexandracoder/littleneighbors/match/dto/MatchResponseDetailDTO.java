package com.alexandracoder.littleneighbors.match.dto;

import com.alexandracoder.littleneighbors.enums.MatchStatus;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record MatchResponseDetailDTO(
        Long matchId,
        MatchStatus status,
        Long myChildId,
        String myChildGender,
        Long theirChildId,
        String theirChildGender,
        Long theirFamilyId,
        String theirFamilyName,
        String theirNeighborhoodName
) {}

