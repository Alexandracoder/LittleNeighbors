package com.alexandracoder.littleneighbors.family.dto;

import com.alexandracoder.littleneighbors.child.dto.ChildSummaryDTO;
import com.alexandracoder.littleneighbors.child.entity.ChildEntity;
import com.alexandracoder.littleneighbors.family.entity.FamilyEntity;
import com.alexandracoder.littleneighbors.neighborhood.entity.NeighborhoodEntity;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class FamilyMapper {

    public FamilyResponseDTO toResponse(FamilyEntity entity) {
        if (entity == null) return null;

        NeighborhoodEntity neighborhood = entity.getNeighborhood();

        Long neighborhoodId = null;
        String streetName = null;
        String postalCode = null;
        String cityName = "No asignado";

        if (neighborhood != null) {
            neighborhoodId = neighborhood.getId();
            streetName = neighborhood.getStreetName();
            postalCode = neighborhood.getPostalCode();
            if (neighborhood.getCity() != null) {
                cityName = neighborhood.getCity().getName();
            }
        }

        List<ChildSummaryDTO> children = (entity.getChildren() == null)
                ? Collections.emptyList()
                : entity.getChildren().stream()
                .map(this::toChildSummary)
                .toList();

        return new FamilyResponseDTO(
                entity.getId(),
                entity.getRepresentativeName(),
                entity.getFamilyName(),
                entity.getDescription(),
                entity.getProfilePictureUrl(),
                neighborhoodId,
                streetName,
                postalCode,
                cityName,
                children
        );
    }

    public FamilyResponseDTO toResponseDTO(FamilyEntity entity) {
        return toResponse(entity);
    }

    private ChildSummaryDTO toChildSummary(ChildEntity child) {
        if (child == null) return null;

        String genderName = (child.getGender() != null)
                ? child.getGender().name()
                : "PRENATAL";

        return new ChildSummaryDTO(
                child.getId(),
                genderName,
                child.getAge(),
                child.getLifeStage()
        );
    }

    public FamilyExplorerDTO toExplorerDTO(FamilyEntity family, boolean isLocked) {
        if (family == null) return null;

        List<ChildSummaryDTO> childrenSummaries = family.getChildren() == null
                ? Collections.emptyList()
                : family.getChildren().stream()
                .map(this::toChildSummary)
                .toList();

        List<String> allInterests = family.getChildren() == null
                ? Collections.emptyList()
                : family.getChildren().stream()
                .filter(c -> c.getInterests() != null)
                .flatMap(c -> c.getInterests().stream())
                .map(i -> i.getName())
                .distinct()
                .toList();

        String neighborhoodName = (family.getNeighborhood() != null)
                ? family.getNeighborhood().getName()
                : "No neighborhood";

        return new FamilyExplorerDTO(
                family.getId(),
                family.getFamilyName(),
                neighborhoodName,
                childrenSummaries,
                allInterests,
                family.getDescription(),
                isLocked
        );
    }
}