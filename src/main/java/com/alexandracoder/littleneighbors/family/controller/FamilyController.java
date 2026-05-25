package com.alexandracoder.littleneighbors.family.controller;

import com.alexandracoder.littleneighbors.family.dto.FamilyRequestDTO;
import com.alexandracoder.littleneighbors.family.dto.FamilyResponseDTO;
import com.alexandracoder.littleneighbors.family.dto.OnboardingResponseDTO;
import com.alexandracoder.littleneighbors.family.service.FamilyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/families")
@RequiredArgsConstructor
public class FamilyController {

    private final FamilyService familyService;

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Create a new family", description = "Creates a family profile and upgrades user role to FAMILY.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Family created and tokens refreshed"),
            @ApiResponse(responseCode = "400", description = "User already has a family or invalid data")
    })
    public ResponseEntity<OnboardingResponseDTO> createFamily(
            Principal principal,
            @Valid @RequestBody FamilyRequestDTO dto) {


        OnboardingResponseDTO response = familyService.createFamily(dto, principal.getName());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/explore")
    @PreAuthorize("hasRole('FAMILY')")
    public ResponseEntity<List<FamilyResponseDTO>> explore(
            Principal principal,
            @RequestParam(required = false) Long currentChildId,
            @RequestParam(required = false) List<Long> interestIds,
            @RequestParam(required = false) Integer minAge,
            @RequestParam(required = false) Integer maxAge) {

        return ResponseEntity.ok(familyService.explorePlaymateFamilies(
                principal.getName(), currentChildId, interestIds, minAge, maxAge));
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
            @Valid @RequestBody FamilyRequestDTO dto) {
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