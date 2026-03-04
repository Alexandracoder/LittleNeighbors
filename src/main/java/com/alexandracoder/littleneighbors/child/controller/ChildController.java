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

    @Operation(summary = "Get logged-in family's children",
            description = "Returns a list of all children belonging to the authenticated family.",
            security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/my-children")
    @PreAuthorize("hasRole('FAMILY')")
    public ResponseEntity<List<ChildResponseDTO>> getMyChildren(Principal principal) {
        // Usamos principal.getName() para que el Service solo busque lo que es mío
        return ResponseEntity.ok(childService.findAllByFamilyEmail(principal.getName()));
    }


    @Operation(summary = "List all children (admin only)",
            description = "Returns a summarized list of all children.",
            security = @SecurityRequirement(name = "bearerAuth"))
    @GetMapping("/summaries")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<ChildSummaryDTO>> getAllSummaries() {
        return ResponseEntity.ok(childService.getAllSummaries());
    }

    @Operation(summary = "Create a child",
            description = "Creates a new child for the logged-in user's family.",
            security = @SecurityRequirement(name = "bearerAuth"))
    @PostMapping
    @PreAuthorize("hasRole('FAMILY')")
    public ResponseEntity<ChildResponseDTO> createChild(
            Principal principal,
            @Valid @RequestBody ChildRequestDTO dto) {
        return ResponseEntity.ok(childService.create(dto, principal.getName()));
    }
}
