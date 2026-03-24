package com.alexandracoder.littleneighbors.family.dto;

import com.alexandracoder.littleneighbors.auth.service.AuthService;
import com.alexandracoder.littleneighbors.child.dto.ChildSummaryDTO;
import com.alexandracoder.littleneighbors.child.entity.ChildEntity;
import com.alexandracoder.littleneighbors.family.entity.FamilyEntity;
import com.alexandracoder.littleneighbors.neighborhood.entity.NeighborhoodEntity;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class FamilyMapper {
    public FamilyResponseDTO toResponse(FamilyEntity entity) {
        if (entity == null) return null;

        NeighborhoodEntity neighborhood = entity.getNeighborhood();

        Long neighborhoodId = (neighborhood != null) ? neighborhood.getId() : null;
        String streetName = (neighborhood != null) ? neighborhood.getStreetName() : null;
        String postalCode = (neighborhood != null) ? neighborhood.getPostalCode() : null;
        String cityName = (neighborhood != null && neighborhood.getCity() != null)
                ? neighborhood.getCity().getName()
                : null;

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
                child.getLifeStage() != null ? child.getLifeStage(): null
        );
    }

    public FamilyExplorerDTO toExplorerDTO(FamilyEntity family, boolean isLocked) {
        if (family == null) return null;

        List<String> interests = family.getChildren() == null
                ? Collections.emptyList()
                : family.getChildren().stream()
                .flatMap(child -> child.getInterests().stream())
                .map(interest -> interest.getName())
                .distinct()
                .toList();

        List<String> childStages = family.getChildren() == null
                ? Collections.emptyList()
                : family.getChildren().stream()
                .map(child -> child.getLifeStage() != null ? child.getLifeStage().name() : "UNKNOWN")
                .toList();

        String neighborhoodName = (family.getNeighborhood() != null)
                ? family.getNeighborhood().getName()
                : "No neighborhood assigned";

        return new FamilyExplorerDTO(
                family.getId(),
                family.getFamilyName(),
                neighborhoodName,
                childStages,
                interests,
                family.getDescription(),
                isLocked
        );
    }
}
