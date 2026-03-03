package com.alexandracoder.littleneighbors.interest.dto;

import com.alexandracoder.littleneighbors.enums.InterestType;

public record InterestResponseDTO(
        Long id, String name,
        InterestType type

) {
}
