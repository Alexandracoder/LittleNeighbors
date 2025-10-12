package com.alexandracoder.littleneighbors.child.dto;

import com.alexandracoder.littleneighbors.enums.Gender;

public record ChildResponseDTO(
        Long id,
        Gender gender,
        Long familyId

) {
}
