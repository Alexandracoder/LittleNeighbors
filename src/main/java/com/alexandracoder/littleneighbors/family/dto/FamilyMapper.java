package com.alexandracoder.littleneighbors.family.dto;

import com.alexandracoder.littleneighbors.child.dto.ChildSummaryDTO;
import com.alexandracoder.littleneighbors.child.entity.ChildEntity;
import com.alexandracoder.littleneighbors.family.entity.FamilyEntity;
import com.alexandracoder.littleneighbors.neighborhood.entity.NeighborhoodEntity;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Random;

@Component
public class FamilyMapper {

    // Radio aproximado del jitter alrededor del centroide del barrio, en
    // grados (~0.003 grados ~= 250-300m en Valencia). Suficiente para que no
    // se amontonen los pines de varias familias del mismo barrio exactamente
    // en el mismo punto, sin llegar a implicar una dirección real.
    private static final double JITTER_DEGREES = 0.003;

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

        double[] coordinates = resolveCoordinates(entity, neighborhood);

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
                children,
                coordinates[0],
                coordinates[1]
        );
    }

    /**
     * Devuelve [latitude, longitude] para pintar el pin de esta familia en
     * el mapa. Si la familia tiene coordenadas propias, se usan tal cual.
     * Si no (el caso normal: no pedimos dirección exacta por privacidad),
     * se aproxima al centroide de su barrio con un pequeño desplazamiento
     * aleatorio pero ESTABLE (semilla = id de familia), para que el pin no
     * salte de sitio cada vez que se recarga la página.
     */
    private double[] resolveCoordinates(FamilyEntity entity, NeighborhoodEntity neighborhood) {
        if (entity.getLatitude() != null && entity.getLongitude() != null) {
            return new double[]{entity.getLatitude(), entity.getLongitude()};
        }

        if (neighborhood == null || neighborhood.getLatitude() == null || neighborhood.getLongitude() == null) {
            return new double[]{Double.NaN, Double.NaN};
        }

        long seed = entity.getId() != null ? entity.getId() : 0L;
        Random jitterRandom = new Random(seed);

        double latJitter = (jitterRandom.nextDouble() * 2 - 1) * JITTER_DEGREES;
        double lngJitter = (jitterRandom.nextDouble() * 2 - 1) * JITTER_DEGREES;

        return new double[]{
                neighborhood.getLatitude() + latJitter,
                neighborhood.getLongitude() + lngJitter
        };
    }

    private ChildSummaryDTO toChildSummary(ChildEntity child) {
        if (child == null) return null;

        return new ChildSummaryDTO(
                child.getId(),
                child.getNickname(),
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