package com.alexandracoder.littleneighbors.family.dto;

import com.alexandracoder.littleneighbors.child.dto.ChildSummaryDTO;
import com.alexandracoder.littleneighbors.child.entity.ChildEntity;
import com.alexandracoder.littleneighbors.family.entity.FamilyEntity;
import com.alexandracoder.littleneighbors.neighborhood.entity.NeighborhoodEntity;

import java.util.Collections;
import java.util.List;

public class FamilyMapper {

    public static FamilyResponseDTO toResponse(FamilyEntity entity) {
        NeighborhoodEntity neighborhood = entity.getNeighborhood();

        String neighborhoodName = neighborhood != null ? neighborhood.getName() : null;
        String streetName = neighborhood != null ? neighborhood.getStreetName() : null;
        String postalCode = neighborhood != null ? neighborhood.getPostalCode() : null;
        String cityName = (neighborhood != null && neighborhood.getCity() != null)
                ? neighborhood.getCity().getName()
                : null;

        List<ChildSummaryDTO> children = entity.getChildren() == null
                ? Collections.emptyList()
                : entity.getChildren().stream()
                .map(FamilyMapper::toChildSummary)
                .toList();

        return new FamilyResponseDTO(
                entity.getId(),
                entity.getRepresentativeName(),
                entity.getFamilyName(),
                entity.getDescription(),
                entity.getProfilePictureUrl(),
                neighborhoodName,
                streetName,
                postalCode,
                cityName,
                children
        );
    }

    private static ChildSummaryDTO toChildSummary(ChildEntity child) {
        if (child == null) return null;
        return new ChildSummaryDTO(
                child.getId(),
                child.getAge(),
                child.getGender()
        );
    }
}


