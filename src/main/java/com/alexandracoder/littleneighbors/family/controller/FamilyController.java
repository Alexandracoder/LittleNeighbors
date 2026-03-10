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
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/families")
@RequiredArgsConstructor
public class FamilyController {

    private final FamilyService familyService;
    private final UserDetailsService userDetailsService;
    private final JwtService jwtService;

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
        // Usamos el email del usuario logueado (principal.getName())
        // para recuperar SU familia.
        return ResponseEntity.ok(familyService.getFamilyByEmail(principal.getName()));
    }
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('FAMILY')")
    public ResponseEntity<FamilyResponseDTO> getFamilyById(
            @PathVariable Long id,
            Principal principal) {

        return ResponseEntity.ok(familyService.getFamilyById(id, principal.getName()));
    }


    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<FamilyAuthResponseDTO> createFamily(
            Principal principal,
            @RequestBody FamilyRequestDTO dto) {

        FamilyResponseDTO familyResponse = familyService.createFamily(dto, principal.getName());

        UserDetails userDetails = userDetailsService.loadUserByUsername(principal.getName());

        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();

        Map<String, Object> claims = Map.of("roles", roles);
        String newAccessToken = jwtService.generateAccessToken(userDetails.getUsername(), claims);
        String newRefreshToken = jwtService.generateRefreshToken(userDetails.getUsername());

        return ResponseEntity.ok(new FamilyAuthResponseDTO(
                familyResponse,
                newAccessToken,
                newRefreshToken
        ));
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
    @Operation(summary = "List all families (admin only)",
            description = "Returns a paginated list of all families.",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Families successfully retrieved"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<Page<FamilyResponseDTO>> getAllFamilies(@ParameterObject Pageable pageable) {

        return ResponseEntity.ok(familyService.getAllFamilies(pageable));
    }
}
