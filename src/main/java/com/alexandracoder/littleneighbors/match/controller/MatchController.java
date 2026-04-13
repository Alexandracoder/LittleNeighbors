package com.alexandracoder.littleneighbors.match.controller;

import com.alexandracoder.littleneighbors.family.dto.FamilyExplorerDTO;
import com.alexandracoder.littleneighbors.family.dto.FamilyMapper;
import com.alexandracoder.littleneighbors.family.entity.FamilyEntity;
import com.alexandracoder.littleneighbors.match.dto.MatchRequestDTO;
import com.alexandracoder.littleneighbors.match.dto.MatchResponseDetailDTO;
import com.alexandracoder.littleneighbors.match.dto.mapper.MatchMapper;
import com.alexandracoder.littleneighbors.match.entity.MatchEntity;
import com.alexandracoder.littleneighbors.match.service.MatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/matches")
@RequiredArgsConstructor
public class MatchController {

    private final MatchService matchService;
    private final MatchMapper matchMapper;
    private final FamilyMapper familyMapper;

    @GetMapping("/explorer")
    public ResponseEntity<List<FamilyExplorerDTO>> getExplorer(
            @RequestParam(required = false) Long neighborhoodId,
            @RequestParam(defaultValue = "0") int minAge,
            @RequestParam(defaultValue = "18") int maxAge,
            @RequestParam(required = false) List<Long> interestIds,
            @RequestParam(required = false) Long currentChildId) {

        if (neighborhoodId == null) {
            return ResponseEntity.ok(new ArrayList<>());
        }

        List<Long> filterInterests = (interestIds == null) ? new ArrayList<>() : interestIds;
        List<FamilyEntity> families = matchService.findCompatibleFamilies(
                neighborhoodId, minAge, maxAge, filterInterests, currentChildId
        );

        boolean isUserLocked = currentChildId != null && matchService.hasActiveMatchThisWeek(currentChildId);

        List<FamilyExplorerDTO> response = families.stream()
                .map(f -> familyMapper.toExplorerDTO(f, isUserLocked))
                .toList();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/request")
    public ResponseEntity<?> requestMatch(@RequestBody MatchRequestDTO request) {
        try {
            MatchEntity match = matchService.requestMatch(
                    request.initiatorChildId(),
                    request.targetChildId()
            );
            return ResponseEntity.ok(matchMapper.toResponseDTO(match));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Error: " + e.getMessage());
        }
    }

    @GetMapping("/my-matches")
    @PreAuthorize("hasRole('FAMILY')")
    public ResponseEntity<List<MatchResponseDetailDTO>> getMyMatches(java.security.Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(401).build();
        }
        List<MatchResponseDetailDTO> matches = matchService.getMatchesForUser(principal.getName());
        return ResponseEntity.ok(matches);
    }

    @PatchMapping("/{id}/respond")
    public ResponseEntity<Void> respondToMatch(
            @PathVariable Long id,
            @RequestParam com.alexandracoder.littleneighbors.enums.MatchStatus status,
            java.security.Principal principal) {

        matchService.respondToMatch(id, status, principal.getName());
        return ResponseEntity.noContent().build();
    }

    // ESTE ES EL MÉTODO QUE EL FRONTEND ESTÁ BUSCANDO
    @PostMapping("/{id}/confirm")
    @PreAuthorize("hasRole('FAMILY')")
    public ResponseEntity<Void> confirmMatch(
            @PathVariable Long id,
            java.security.Principal principal) {

        if (principal == null) {
            return ResponseEntity.status(401).build();
        }

        matchService.confirmMatch(id, principal.getName());
        return ResponseEntity.ok().build();
    }
}