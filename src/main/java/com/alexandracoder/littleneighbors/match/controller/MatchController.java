package com.alexandracoder.littleneighbors.match.controller;

import com.alexandracoder.littleneighbors.match.dto.MatchRequestDTO;
import com.alexandracoder.littleneighbors.match.service.MatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/matches")
@RequiredArgsConstructor
public class MatchController {

    private final MatchService matchService;

    @PostMapping("/request")
    public ResponseEntity<?> requestMatch(@RequestBody MatchRequestDTO request) {
        try {
            return ResponseEntity.ok(matchService.requestMatch(
                    request.initiatorChildId(),
                    request.targetChildId()
            ));
        } catch (IllegalStateException e) {
            // Esto captura la restricción de "1 match por semana" o "diferente barrio"
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            // Manejo genérico para otros errores inesperados
            return ResponseEntity.internalServerError().body("An error occurred: " + e.getMessage());
        }
    }
}