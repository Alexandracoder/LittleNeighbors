package com.alexandracoder.littleneighbors.neighborhood.mapper;

import com.alexandracoder.littleneighbors.neighborhood.dto.NeighborhoodResponseDTO;
import com.alexandracoder.littleneighbors.neighborhood.entity.NeighborhoodEntity;
import org.springframework.stereotype.Component;

@Component
public class NeighborhoodMapper {

    public NeighborhoodResponseDTO toResponseDTO(NeighborhoodEntity entity) {
        return new NeighborhoodResponseDTO(
                entity.getId(),
                entity.getName(),
                entity.getStreetName(),
                entity.getPostalCode(),
                entity.getCity() != null ? entity.getCity().getId() : null,
                entity.getCity() != null ? entity.getCity().getName() : null
        );
    }
}