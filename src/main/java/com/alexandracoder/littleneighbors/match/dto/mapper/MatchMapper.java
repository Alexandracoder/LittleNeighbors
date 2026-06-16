package com.alexandracoder.littleneighbors.match.dto.mapper;

import com.alexandracoder.littleneighbors.match.dto.MatchResponseDTO;
import com.alexandracoder.littleneighbors.match.entity.MatchEntity;
import org.springframework.stereotype.Component;

@Component
public class MatchMapper {

    public MatchResponseDTO toResponseDTO(MatchEntity entity) {
        if (entity == null) {
            return null;
        }

        return new MatchResponseDTO(
                entity.getId(),
                entity.getChildRequest().getFamily().getUser().getId(),
                entity.getChildTarget().getFamily().getId(),
                entity.getStatus()
        );
    }
}
