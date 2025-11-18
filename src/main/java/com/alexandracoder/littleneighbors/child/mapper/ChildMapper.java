package com.alexandracoder.littleneighbors.child.mapper;

import com.alexandracoder.littleneighbors.child.dto.ChildResponseDTO;
import com.alexandracoder.littleneighbors.child.dto.ChildSummaryDTO;
import com.alexandracoder.littleneighbors.child.entity.ChildEntity;
import org.springframework.stereotype.Component;

@Component
public class ChildMapper {

    public ChildResponseDTO toResponseDTO(ChildEntity entity) {
        return new ChildResponseDTO(
                entity.getId(),
                entity.getGender(),
                entity.getFamily() != null ? entity.getFamily().getId() : null
        );
    }

    public ChildSummaryDTO toSummaryDTO(ChildEntity entity) {
        return new ChildSummaryDTO(
                entity.getId(),
                entity.getGender(),
                entity.getAge()
        );
    }
}