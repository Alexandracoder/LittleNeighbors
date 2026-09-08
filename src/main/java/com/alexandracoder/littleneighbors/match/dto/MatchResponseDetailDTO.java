package com.alexandracoder.littleneighbors.match.dto;

import com.alexandracoder.littleneighbors.enums.MatchStatus;
import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record MatchResponseDetailDTO(
        Long matchId,
        MatchStatus status,
        Long myChildId,
        String myChildGender,
        Long theirChildId,
        String theirChildGender,
        Long theirFamilyId,
        String theirFamilyName,
        // Antes solo se mandaba el nombre de familia (p.ej. "Familia
        // Pérez"), nunca el de la persona con la que hablas de verdad —
        // ni en la lista de familias, ni en el chat.
        String theirRepresentativeName,
        String theirNeighborhoodName
) {}

