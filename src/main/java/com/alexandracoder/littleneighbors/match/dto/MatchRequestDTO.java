package com.alexandracoder.littleneighbors.match.dto;

import jakarta.validation.constraints.NotNull;

public record MatchRequestDTO(
        @NotNull Long initiatorChildId,
        @NotNull Long targetChildId
) {}
