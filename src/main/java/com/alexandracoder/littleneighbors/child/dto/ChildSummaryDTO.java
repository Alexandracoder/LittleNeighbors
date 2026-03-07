package com.alexandracoder.littleneighbors.child.dto;

public record ChildSummaryDTO(
        Long id,
        String gender,
        Integer age,
        com.alexandracoder.littleneighbors.enums.LifeStage lifeStage) {
}
