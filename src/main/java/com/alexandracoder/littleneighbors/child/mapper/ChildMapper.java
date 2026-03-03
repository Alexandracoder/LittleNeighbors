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

        List<InterestResponseDTO> interestDTOs = entity.getInterests().stream()
                .map(i -> new InterestResponseDTO(i.getId(), i.getName(), i.getType()))
                .toList();

        Long familyId = (entity.getFamily() != null) ? entity.getFamily().getId() : null;

        return new ChildResponseDTO(
                entity.getId(),
                entity.getGender(),
                entity.getBirthDate(),
                entity.getAge(),
                interestDTOs,
                familyId
        );
    }


    public ChildSummaryDTO toSummaryDTO(ChildEntity entity) {
        if (entity == null) return null;

        return new ChildSummaryDTO(
                entity.getId(),
                entity.getGender().name(), // Convertimos el Enum a String
                entity.getAge()
        );
    }
}