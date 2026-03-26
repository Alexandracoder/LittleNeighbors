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
                @RequestParam Long neighborhoodId,
                @RequestParam int minAge,
                @RequestParam int maxAge,
                @RequestParam(required = false) List<Long> interestIds,
                @RequestParam Long currentChildId) {

            // Filtramos familias compatibles
            List<FamilyEntity> families = matchService.findCompatibleFamilies(
                    neighborhoodId, minAge, maxAge, interestIds
            );

            // Verificamos si el usuario ya gastó su "bala" semanal
            boolean isUserLocked = matchService.hasActiveMatchThisWeek(currentChildId);

            // Convertimos a DTOs anónimos (sin nombres de niños)
            List<FamilyExplorerDTO> response = families.stream()
                    .map(f -> familyMapper.toExplorerDTO(f, isUserLocked))
                    .toList();

            return ResponseEntity.ok(response);
        }

        // 2. ENDPOINT PARA SOLICITAR EL MATCH (Botón "Match to Chat")
        @PostMapping("/request")
        public ResponseEntity<?> requestMatch(@RequestBody MatchRequestDTO request) {
            try {
                // Ejecutamos la lógica de negocio
                MatchEntity match = matchService.requestMatch(
                        request.initiatorChildId(),
                        request.targetChildId()
                );

                // Devolvemos el DTO de respuesta limpio
                return ResponseEntity.ok(matchMapper.toResponseDTO(match));

            } catch (IllegalStateException e) {
                // Aquí capturamos: "Solo un match por semana" o "Diferente barrio"
                return ResponseEntity.badRequest().body(e.getMessage());
            } catch (Exception e) {
                return ResponseEntity.internalServerError().body("Error inesperado: " + e.getMessage());
            }
        }
    }