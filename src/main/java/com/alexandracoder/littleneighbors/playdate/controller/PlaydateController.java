package com.alexandracoder.littleneighbors.playdate.controller;

import com.alexandracoder.littleneighbors.playdate.dto.PlaydateRequestDTO;
import com.alexandracoder.littleneighbors.playdate.entity.PlaydateEntity;
import com.alexandracoder.littleneighbors.playdate.service.PlaydateService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/playdates")
@RequiredArgsConstructor
public class PlaydateController {

    private final PlaydateService playdateService;


    @PostMapping
    @PreAuthorize("hasRole('FAMILY')")
    public ResponseEntity<PlaydateEntity> createPlaydate(
            @RequestBody PlaydateRequestDTO dto,
            Principal principal) {

        PlaydateEntity playdate = playdateService.createPlaydate(dto, principal.getName());
        return ResponseEntity.ok(playdate);
    }

    @GetMapping("/match/{matchId}")
    @PreAuthorize("hasRole('FAMILY')")
    public ResponseEntity<List<PlaydateEntity>> getPlaydatesByMatch(@PathVariable Long matchId) {
        List<PlaydateEntity> playdates = playdateService.findByMatchId(matchId);
        return ResponseEntity.ok(playdates);
    }

    @GetMapping("/family/{familyId}")
    @PreAuthorize("hasRole('FAMILY')")
    public ResponseEntity<List<PlaydateEntity>> getPlaydatesByFamily(@PathVariable Long familyId) {
        List<PlaydateEntity> playdates = playdateService.findAllByFamily(familyId);
        return ResponseEntity.ok(playdates);
    }
}