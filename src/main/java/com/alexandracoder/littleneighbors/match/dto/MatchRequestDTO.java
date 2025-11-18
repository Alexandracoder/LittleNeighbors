package com.alexandracoder.littleneighbors.match.dto;

import jakarta.validation.constraints.NotNull;

public record MatchRequestDTO(
        @NotNull Long searcherUserId,
        @NotNull Long targetFamilyId
) {}
