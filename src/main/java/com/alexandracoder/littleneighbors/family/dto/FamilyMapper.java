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
                .map(this::toChildSummary) // Llamamos a la instancia, no a FamilyMapper::toChildSummary
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

    private ChildSummaryDTO toChildSummary(ChildEntity child) {
        if (child == null) return null;

        // Si el género es nulo (embarazo), devolvemos "PRENATAL" o un valor por defecto
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

