package com.alexandracoder.littleneighbors.playdate.controller;

import com.alexandracoder.littleneighbors.playdate.dto.PlaydateRequestDTO;
import com.alexandracoder.littleneighbors.playdate.entity.PlaydateEntity;
import com.alexandracoder.littleneighbors.playdate.service.PlaydateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/playdates")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class PlaydateController {

    private final PlaydateService playdateService; // Inyectamos la interfaz

    @PostMapping
    public ResponseEntity<PlaydateEntity> create(@RequestBody PlaydateRequestDTO dto) {
        return ResponseEntity.ok(playdateService.createPlaydate(dto));
    }

    @GetMapping("/family/{familyId}")
    public ResponseEntity<List<PlaydateEntity>> getFamilyPlaydates(@PathVariable Long familyId) {
        return ResponseEntity.ok(playdateService.findAllByFamily(familyId));
    }
}