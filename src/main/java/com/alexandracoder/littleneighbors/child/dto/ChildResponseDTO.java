package com.alexandracoder.littleneighbors.child.dto;

import com.alexandracoder.littleneighbors.enums.Gender;
import com.alexandracoder.littleneighbors.enums.LifeStage;
import com.alexandracoder.littleneighbors.interest.dto.InterestResponseDTO;
import java.time.LocalDate;
import java.util.List;

public record ChildResponseDTO(
        Long id,
        LocalDate birthDate,
        LocalDate dueDate,
        LifeStage lifeStage,
        Gender gender,
        List<InterestResponseDTO> interests,
        boolean isPrenatal,
        boolean pregnancySupport,
        Long familyId
) {}