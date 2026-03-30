package com.alexandracoder.littleneighbors.child.mapper;

import com.alexandracoder.littleneighbors.child.dto.ChildResponseDTO;
import com.alexandracoder.littleneighbors.child.dto.ChildSummaryDTO;
import com.alexandracoder.littleneighbors.child.entity.ChildEntity;
import com.alexandracoder.littleneighbors.interest.dto.InterestResponseDTO;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ChildMapper {

    public ChildResponseDTO toResponseDTO(ChildEntity entity) {
        if (entity == null) return null;

        List<InterestResponseDTO> interestDTOs = (entity.getInterests() != null)
                ? entity.getInterests().stream()
                .map(i -> new InterestResponseDTO(
                        i.getId(),
                        i.getName(),
                        i.getType(),
                        i.getIcon()
                ))
                .toList()
                : List.of();

        Long familyId = (entity.getFamily() != null) ? entity.getFamily().getId() : null;

        // IMPORTANTE: El orden de estos campos debe ser IDÉNTICO al de tu ChildResponseDTO record
        return new ChildResponseDTO(
                entity.getId(),
                entity.getBirthDate(),
                entity.getDueDate(),
                entity.getLifeStage(),
                entity.getGender(),
                interestDTOs,
                entity.isPrenatal(),
                entity.isPregnancySupport(),
                familyId
        );
    }

    public ChildSummaryDTO toSummaryDTO(ChildEntity entity) {
        if (entity == null) return null;

        return new ChildSummaryDTO(
                entity.getId(),
                entity.getGender() != null ? entity.getGender().name() : null,
                entity.getAge(),
                entity.getLifeStage()
        );
    }
}