package com.alexandracoder.littleneighbors.family.dto;

import com.alexandracoder.littleneighbors.enums.FamilyStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.List;

public record FamilyRequestDTO(
        String userId,
        @NotBlank @Size(max = 255) String representativeName,
        @NotBlank @Size(max = 255) String familyName,
        @NotBlank @Size(max = 1000) String description,
        @Pattern(
                regexp = "^$|^https://[\\w.-]+(:\\d+)?(/[^\\s]*)?$",
                message = "profilePictureUrl must be blank or a valid https:// URL"
        )
        @Size(max = 2048)
        String profilePictureUrl,
        // Uno de los dos es obligatorio (nunca los dos a la vez): si el
        // barrio de la familia no está en la lista de barrios piloto, se
        // manda customLocationName en su lugar. La comprobación de que
        // venga al menos uno vive en FamilyServiceImpl, no aquí, porque
        // Bean Validation no expresa bien "uno de estos dos" en un record.
        Long neighborhoodId,
        @Size(max = 255) String customLocationName,
        FamilyStatus status,
        List<String> familyInterests
) {}


