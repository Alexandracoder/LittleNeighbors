package com.alexandracoder.littleneighbors.neighborhood.controller;

import com.alexandracoder.littleneighbors.neighborhood.dto.NeighborhoodRequestDTO;
import com.alexandracoder.littleneighbors.neighborhood.dto.NeighborhoodResponseDTO;
import com.alexandracoder.littleneighbors.neighborhood.service.NeighborhoodService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/neighborhoods")
@RequiredArgsConstructor
@Tag(name = "Neighborhoods", description = "Manage neighborhoods")
public class NeighborhoodController {

    private final NeighborhoodService neighborhoodService;
    @Operation(summary = "List all neighborhoods",
            description = "Returns a paginated list of all neighborhoods with optional filters.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List successfully returned"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @GetMapping
    @PreAuthorize("hasRole('USER') or hasRole('FAMILY') or hasRole('ADMIN')")
    public ResponseEntity<Page<NeighborhoodResponseDTO>> getAll(
            @Parameter(description = "Filter by name") @RequestParam(required = false) String name,
            @Parameter(description = "Filter by city ID") @RequestParam(required = false) Long cityId,
            @ParameterObject Pageable pageable) {

        return ResponseEntity.ok(neighborhoodService.getAll(name, cityId, pageable));
    }

    @Operation(summary = "Get a neighborhood by ID",
            description = "Returns a single neighborhood by its ID. Accessible by FAMILY and ADMIN.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Neighborhood found"),
            @ApiResponse(responseCode = "404", description = "Neighborhood not found"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('USER') or hasRole('FAMILY') or hasRole('ADMIN')")
    public ResponseEntity<NeighborhoodResponseDTO> getById(
            @Parameter(description = "Neighborhood ID", required = true)
            @PathVariable Long id) {
        return ResponseEntity.ok(neighborhoodService.getById(id));
    }

    @Operation(summary = "Create a neighborhood (admin only)",
            description = "Creates a new neighborhood.",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Neighborhood successfully created"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "City not found")
    })
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<NeighborhoodResponseDTO> create(
            @Valid @RequestBody
            @Parameter(description = "Neighborhood data", required = true)
            NeighborhoodRequestDTO dto) {
        return ResponseEntity.ok(neighborhoodService.create(dto));
    }

    @Operation(summary = "Update a neighborhood (admin only)",
            description = "Updates an existing neighborhood.",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Neighborhood successfully updated"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Neighborhood or city not found")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<NeighborhoodResponseDTO> update(
            @Parameter(description = "Neighborhood ID", required = true)
            @PathVariable Long id,
            @Valid @RequestBody
            @Parameter(description = "Updated neighborhood data", required = true)
            NeighborhoodRequestDTO dto) {
        return ResponseEntity.ok(neighborhoodService.update(id, dto));
    }

    @Operation(summary = "Delete a neighborhood (admin only)",
            description = "Deletes a neighborhood. Fails if it has associated families.",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Neighborhood successfully deleted"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Neighborhood not found"),
            @ApiResponse(responseCode = "409", description = "Neighborhood has associated families")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(
            @Parameter(description = "Neighborhood ID", required = true)
            @PathVariable Long id) {
        neighborhoodService.delete(id);
        return ResponseEntity.noContent().build();
    }
}