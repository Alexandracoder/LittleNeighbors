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

    private final AuthService authService;

    public FamilyMapper(@Lazy AuthService authService) {
        this.authService = authService;
    }

    // Este es tu método principal de mapeo
    public FamilyResponseDTO toResponse(FamilyEntity entity) {
        if (entity == null) return null;

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
                .map(this::toChildSummary)
                .toList();

        return new FamilyResponseDTO(
                entity.getId(),
                entity.getRepresentativeName(),
                entity.getFamilyName(),
                entity.getDescription(),
                entity.getProfilePictureUrl(),
                neighborhood.getId(),
                streetName,
                postalCode,
                cityName,
                children
        );
    }

    // Renombramos o redirigimos toResponseDTO para que apunte a toResponse
    // Esto es lo que piden tus tests que fallaban
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
}

