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
                child.getLifeStage() != null ? child.getLifeStage() : null
        );
    }

    public FamilyExplorerDTO toExplorerDTO(FamilyEntity family, boolean isLocked) {
        if (family == null) return null;

        // Mapeo anónimo de los niños de la familia vecina
        List<ChildSummaryDTO> childrenSummaries = family.getChildren() == null
                ? Collections.emptyList()
                : family.getChildren().stream()
                .map(this::toChildSummary) // Usamos el método que ya tienes que da género y edad
                .toList();

        // Intereses únicos de toda la familia para las etiquetas de la card
        List<String> allInterests = family.getChildren() == null
                ? Collections.emptyList()
                : family.getChildren().stream()
                .filter(c -> c.getInterests() != null)
                .flatMap(c -> c.getInterests().stream())
                .map(i -> i.getName())
                .distinct()
                .toList();

        return new FamilyExplorerDTO(
                family.getId(),
                family.getFamilyName(),
                family.getNeighborhood() != null ? family.getNeighborhood().getName() : "Unknown",
                childrenSummaries,
                allInterests,
                family.getDescription(),
                isLocked
        );
    }
}