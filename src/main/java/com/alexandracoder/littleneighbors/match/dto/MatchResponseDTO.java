package com.alexandracoder.littleneighbors.match.dto;

import com.alexandracoder.littleneighbors.enums.MatchStatus;

public record MatchResponseDTO(
        Long id,
        Long searcherUserId,
        Long targetFamilyId,
        MatchStatus status
) {
}
