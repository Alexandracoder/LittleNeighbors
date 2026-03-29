package com.alexandracoder.littleneighbors.match.controller;

import com.alexandracoder.littleneighbors.family.dto.FamilyExplorerDTO;
import com.alexandracoder.littleneighbors.family.dto.FamilyMapper;
import com.alexandracoder.littleneighbors.family.entity.FamilyEntity;
import com.alexandracoder.littleneighbors.match.dto.MatchRequestDTO;
import com.alexandracoder.littleneighbors.match.dto.mapper.MatchMapper;
import com.alexandracoder.littleneighbors.match.entity.MatchEntity;
import com.alexandracoder.littleneighbors.match.service.MatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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
            @RequestParam(required = false) Long neighborhoodId, // Cambiado a false
            @RequestParam(defaultValue = "0") int minAge,
            @RequestParam(defaultValue = "18") int maxAge,
            @RequestParam(required = false) List<Long> interestIds,
            @RequestParam(required = false) Long currentChildId) {

        // Si no hay barrio, devolvemos una lista vacía con un 200 OK (Clean UI)
        if (neighborhoodId == null) {
            return ResponseEntity.ok(new ArrayList<>());
        }

        List<Long> filterInterests = (interestIds == null) ? new ArrayList<>() : interestIds;

        List<FamilyEntity> families = matchService.findCompatibleFamilies(
                neighborhoodId, minAge, maxAge, filterInterests
        );

        boolean isUserLocked;
        if (currentChildId != null) {
            isUserLocked = matchService.hasActiveMatchThisWeek(currentChildId);
        } else {
            isUserLocked = false;
        }

        List<FamilyExplorerDTO> response = families.stream()
                .distinct()
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
}