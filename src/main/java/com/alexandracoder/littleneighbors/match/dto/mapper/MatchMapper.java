package com.alexandracoder.littleneighbors.match.dto.mapper;

import com.alexandracoder.littleneighbors.match.dto.MatchResponseDTO;
import com.alexandracoder.littleneighbors.match.entity.MatchEntity;
import org.springframework.stereotype.Component;

@Component
public class MatchMapper {

    public MatchResponseDTO toResponseDTO(MatchEntity entity) {
        return new MatchResponseDTO(
                entity.getId(),
                entity.getChildA().getFamily().getUser().getId(), // Quien inicia (Searcher)
                entity.getChildB().getFamily().getId(),          // La familia destino (The Fayes)
                entity.getStatus()
        );
    }
}
