package com.alexandracoder.littleneighbors.child.mapper;

import com.alexandracoder.littleneighbors.child.dto.ChildResponseDTO;
import com.alexandracoder.littleneighbors.child.dto.ChildSummaryDTO;
import com.alexandracoder.littleneighbors.child.entity.ChildEntity;
import org.springframework.stereotype.Component;

@Component
public class ChildMapper {

    public ChildResponseDTO toResponseDTO(ChildEntity entity) {
        if (entity == null) {
            return null;
        }

        Long familyId = (entity.getFamily() != null) ? entity.getFamily().getId() : null;

        return new ChildResponseDTO(
                entity.getId(),
                entity.getGender(),
                familyId
        );
    }

    public ChildSummaryDTO toSummaryDTO(ChildEntity entity) {
        if (entity == null) {
            return null;
        }

        return new ChildSummaryDTO(
                entity.getId(),
                entity.getGender(),
                entity.getAge()
        );
    }
}