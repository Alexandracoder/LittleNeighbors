package com.alexandracoder.littleneighbors.child.mapper;

import com.alexandracoder.littleneighbors.child.dto.ChildResponseDTO;
import com.alexandracoder.littleneighbors.child.dto.ChildRequestDTO;
import com.alexandracoder.littleneighbors.child.dto.ChildSummaryDTO;
import com.alexandracoder.littleneighbors.child.entity.ChildEntity;
import org.springframework.stereotype.Component;

@Component
public class ChildMapper {

    public ChildEntity toEntity(ChildRequestDTO dto) {
        if (dto == null) return null;
        ChildEntity child = new ChildEntity();
        child.setGender(dto.gender());
        return child;
    }

    public ChildResponseDTO toResponseDTO(ChildEntity child) {
        if (child == null) return null;
        return new ChildResponseDTO(
                child.getId(),
                child.getGender(),
                child.getFamily() != null ? child.getFamily().getId() : null
        );
    }

    public ChildSummaryDTO toSummaryDTO(ChildEntity child) {
        if (child == null) return null;
        return new ChildSummaryDTO(
                        child.getId(),
                child.getGender(),
                Integer.valueOf(child.getAge())
                );
    }
}
