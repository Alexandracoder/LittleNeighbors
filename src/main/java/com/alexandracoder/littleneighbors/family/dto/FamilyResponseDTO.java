package com.alexandracoder.littleneighbors.family.dto;

import com.alexandracoder.littleneighbors.child.dto.ChildSummaryDTO;
import com.alexandracoder.littleneighbors.enums.PhotoModerationStatus;

import java.util.List;
public record FamilyResponseDTO(
        Long id,
        String representativeName,
        String familyName,
        String description,
        String profilePictureUrl,
        Long neighborhoodId,
        String neighborhoodName,
        String streetName,
        String postalCode,
        String cityName,
        List<ChildSummaryDTO> children,
        Double latitude,
        Double longitude,
        // Solo se rellena en la vista de la PROPIA familia (toResponse). En
        // la vista pública (toPublicResponse, mapa/explorar/ficha ajena) va
        // siempre a null: a otra familia no le aporta nada saber si TU foto
        // fue rechazada, y no hace falta exponerlo.
        PhotoModerationStatus photoModerationStatus,
        String photoRejectionReason
) {}
