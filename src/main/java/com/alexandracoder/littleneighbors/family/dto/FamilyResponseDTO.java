package com.alexandracoder.littleneighbors.family.dto;

import com.alexandracoder.littleneighbors.child.dto.ChildSummaryDTO;

import java.util.List;

public record FamilyResponseDTO(
        Long id,
        String representativeName,
        String familyName,
        String description,
        String profilePictureUrl,
        String neighborhoodName,
        String streetName,
        String postalCode,
        String cityName,
        List<ChildSummaryDTO> children
) {}
