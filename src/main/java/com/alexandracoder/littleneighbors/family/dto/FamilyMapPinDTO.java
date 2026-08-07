package com.alexandracoder.littleneighbors.family.dto;

// Deliberadamente NO lleva id, nombre, foto ni niños: es lo único que
// puede ver una familia todavía sin verificar en el mapa de explorar.
public record FamilyMapPinDTO(
        Double latitude,
        Double longitude
) {
}
