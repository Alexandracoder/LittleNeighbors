package com.alexandracoder.littleneighbors.family.controller;

import com.alexandracoder.littleneighbors.family.dto.FamilyAuthResponseDTO;
import com.alexandracoder.littleneighbors.family.dto.FamilyRequestDTO;
import com.alexandracoder.littleneighbors.family.dto.FamilyResponseDTO;
import com.alexandracoder.littleneighbors.family.service.FamilyService;
import com.alexandracoder.littleneighbors.security.JwtService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/families")
@RequiredArgsConstructor
public class FamilyController {

    private final FamilyService familyService;
    private final JwtService jwtService;

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Create a new family", description = "Creates a family profile and upgrades user role to FAMILY.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Family created and tokens refreshed"),
            @ApiResponse(responseCode = "400", description = "User already has a family or invalid data")
    })
    public ResponseEntity<FamilyAuthResponseDTO> createFamily(
            Principal principal,
            @RequestBody FamilyRequestDTO dto) {

        // 1. Llamada al servicio (aquí se crea la familia y se cambia el rol en DB)
        FamilyResponseDTO familyResponse = familyService.createFamily(dto, principal.getName());

        // 2. Definimos los nuevos roles manualmente.
        // Como acabamos de crear la familia con éxito, sabemos que el usuario ahora es ROLE_FAMILY.
        // Esto evita llamar a UserDetailsService y posibles errores de sincronización/500.
        List<String> roles = List.of("ROLE_FAMILY");

        // 3. Generamos los nuevos claims y tokens
        Map<String, Object> claims = Map.of("roles", roles);
        String newAccessToken = jwtService.generateAccessToken(principal.getName(), claims);
        String newRefreshToken = jwtService.generateRefreshToken(principal.getName());

        return ResponseEntity.ok(new FamilyAuthResponseDTO(
                familyResponse,
                newAccessToken,
                newRefreshToken
        ));
    }

    @GetMapping("/explore")
    @PreAuthorize("hasRole('FAMILY')")
    public ResponseEntity<List<FamilyResponseDTO>> explore(
            Principal principal,
            @RequestParam(required = false) List<Long> interestIds,
            @RequestParam(required = false) Integer minAge,
            @RequestParam(required = false) Integer maxAge) {

        return ResponseEntity.ok(familyService.explorePlaymateFamilies(
                principal.getName(), interestIds, minAge, maxAge));
    }

    @GetMapping("/my-family")
    @PreAuthorize("hasRole('FAMILY')")
    public ResponseEntity<FamilyResponseDTO> getMyFamily(Principal principal) {
        return ResponseEntity.ok(familyService.getFamilyByEmail(principal.getName()));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FAMILY')")
    public ResponseEntity<FamilyResponseDTO> getFamilyById(
            @PathVariable Long id,
            Principal principal) {
        return ResponseEntity.ok(familyService.getFamilyById(id, principal.getName()));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('FAMILY')")
    public ResponseEntity<FamilyResponseDTO> updateFamily(
            @PathVariable Long id,
            Principal principal,
            @RequestBody FamilyRequestDTO dto) {
        return ResponseEntity.ok(familyService.updateFamily(id, dto, principal.getName()));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FAMILY')")
    public ResponseEntity<Void> deleteFamily(
            @PathVariable Long id,
            Principal principal) {
        familyService.deleteFamily(id, principal.getName());
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "List all families (admin only)", security = @SecurityRequirement(name = "bearerAuth"))
    public ResponseEntity<Page<FamilyResponseDTO>> getAllFamilies(@ParameterObject Pageable pageable) {
        return ResponseEntity.ok(familyService.getAllFamilies(pageable));
    }
}
