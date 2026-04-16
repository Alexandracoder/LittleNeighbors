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
        String neighborhoodName = "Not assigned";
        String streetName = "Not assigned";
        String postalCode = "N/A";
        String cityName = "Not assigned";

        if (neighborhood != null) {
            neighborhoodId = neighborhood.getId();
            neighborhoodName = (neighborhood.getName() != null) ? neighborhood.getName() : "Not assigned";
            streetName = (neighborhood.getStreetName() != null) ? neighborhood.getStreetName() : "Not assigned";
            postalCode = (neighborhood.getPostalCode() != null) ? neighborhood.getPostalCode() : "N/A";

            if (neighborhood.getCity() != null && neighborhood.getCity().getName() != null) {
                cityName = neighborhood.getCity().getName();
            }
        }

        List<ChildSummaryDTO> children = (entity.getChildren() != null)
                ? entity.getChildren().stream()
                .filter(c -> c != null)
                .map(this::toChildSummary)
                .toList()
                : Collections.emptyList();

        return new FamilyResponseDTO(
                entity.getId(),
                entity.getRepresentativeName(),
                entity.getFamilyName(),
                entity.getDescription(),
                entity.getProfilePictureUrl(),
                neighborhoodId,
                neighborhoodName,
                streetName,
                postalCode,
                cityName,
                children
        );
    }


    private ChildSummaryDTO toChildSummary(ChildEntity child) {
        if (child == null) return null;

        return new ChildSummaryDTO(
                child.getId(),
                child.getGender() != null ? child.getGender().name() : "PRENATAL",
                child.getAge(),
                child.getLifeStage()
        );
    }

    public FamilyExplorerDTO toExplorerDTO(FamilyEntity family, boolean isLocked) {
        if (family == null) return null;

        List<ChildSummaryDTO> childrenSummaries = (family.getChildren() != null)
                ? family.getChildren().stream().map(this::toChildSummary).toList()
                : Collections.emptyList();

        List<String> allInterests = (family.getChildren() != null)
                ? family.getChildren().stream()
                .filter(c -> c != null && c.getInterests() != null)
                .flatMap(c -> c.getInterests().stream())
                .filter(i -> i != null && i.getName() != null)
                .map(i -> i.getName())
                .distinct()
                .toList()
                : Collections.emptyList();

        String neighborhoodName = (family.getNeighborhood() != null && family.getNeighborhood().getName() != null)
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