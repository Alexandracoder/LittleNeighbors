package com.alexandracoder.littleneighbors.child.dto;

import com.alexandracoder.littleneighbors.enums.Gender;

public record ChildSummaryDTO(
        Long id,
        Gender gender,
        Integer age
) {
}
