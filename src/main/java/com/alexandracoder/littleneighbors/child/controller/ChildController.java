package com.alexandracoder.littleneighbors.child.controller;

import com.alexandracoder.littleneighbors.child.dto.ChildRequestDTO;
import com.alexandracoder.littleneighbors.child.dto.ChildResponseDTO;
import com.alexandracoder.littleneighbors.child.dto.ChildSummaryDTO;
import com.alexandracoder.littleneighbors.child.service.ChildService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/children")
@RequiredArgsConstructor
@Tag(name = "Children", description = "Manage children, their families, and interests")
public class ChildController {

    private final ChildService childService;

    @Operation(summary = "List all children (admin only)",
            description = "Returns a summarized list of all children.",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List successfully returned"),
            @ApiResponse(responseCode = "403", description = "Access denied")
    })
    @GetMapping("/summaries")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ChildSummaryDTO>> getAllSummaries() {
        return ResponseEntity.ok(childService.getAllSummaries());
    }

    @Operation(summary = "Create a child",
            description = "Creates a new child for the logged-in user's family.",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Child successfully created"),
            @ApiResponse(responseCode = "400", description = "Invalid request data"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Family not found")
    })
    @PostMapping
    @PreAuthorize("hasRole('FAMILY')")
    public ResponseEntity<ChildResponseDTO> createChild(
            Principal principal,
            @Valid @RequestBody
            @Parameter(description = "Child data to create", required = true)
            ChildRequestDTO dto) {

        return ResponseEntity.ok(childService.create(dto, principal.getName()));
    }

    @Operation(summary = "Update a child",
            description = "Updates a child owned by the logged-in family.",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Child successfully updated"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Child not found")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('FAMILY')")
    public ResponseEntity<ChildResponseDTO> updateChild(
            Principal principal,
            @Parameter(description = "Child ID to update", required = true)
            @PathVariable Long id,
            @Valid @RequestBody
            @Parameter(description = "Updated child data", required = true)
            ChildRequestDTO dto) {

        return ResponseEntity.ok(childService.update(id, dto, principal.getName()));
    }

    @Operation(summary = "Get a child by ID",
            description = "Returns a child owned by the logged-in family.",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Child found"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Child not found")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('FAMILY')")
    public ResponseEntity<ChildResponseDTO> getChildById(
            Principal principal,
            @Parameter(description = "Child ID", required = true)
            @PathVariable Long id) {

        return ResponseEntity.ok(childService.getById(id, principal.getName()));
    }

    @Operation(summary = "Delete a child",
            description = "Deletes a child owned by the logged-in family.",
            security = @SecurityRequirement(name = "bearerAuth"))
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Child successfully deleted"),
            @ApiResponse(responseCode = "403", description = "Access denied"),
            @ApiResponse(responseCode = "404", description = "Child not found")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('FAMILY') or hasRole('ADMIN')")
    public ResponseEntity<Void> deleteChild(
            Principal principal,
            @Parameter(description = "Child ID to delete", required = true)
            @PathVariable Long id) {

        childService.delete(id, principal.getName());
        return ResponseEntity.noContent().build();
    }
}
