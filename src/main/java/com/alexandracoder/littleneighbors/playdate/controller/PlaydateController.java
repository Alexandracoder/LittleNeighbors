package com.alexandracoder.littleneighbors.playdate.controller;

import com.alexandracoder.littleneighbors.playdate.dto.PlaydateRequestDTO;
import com.alexandracoder.littleneighbors.playdate.dto.PlaydateResponseDTO;
import com.alexandracoder.littleneighbors.playdate.entity.PlaydateEntity;
import com.alexandracoder.littleneighbors.playdate.service.PlaydateService;
import com.alexandracoder.littleneighbors.shared.exceptions.ResourceNotFoundException;
import com.alexandracoder.littleneighbors.user.entity.UserEntity;
import com.alexandracoder.littleneighbors.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/playdates")
@RequiredArgsConstructor
public class PlaydateController {

    private final PlaydateService playdateService;
    private final UserRepository userRepository;

    @PostMapping
    @PreAuthorize("hasRole('FAMILY')")
    public ResponseEntity<PlaydateEntity> createPlaydate(
            @RequestBody PlaydateRequestDTO dto,
            Principal principal) {
        return ResponseEntity.ok(playdateService.createPlaydate(dto, principal.getName()));
    }

    @GetMapping("/match/{matchId}")
    @PreAuthorize("hasRole('FAMILY')")
    public ResponseEntity<List<PlaydateEntity>> getPlaydatesByMatch(@PathVariable Long matchId) {
        return ResponseEntity.ok(playdateService.findByMatchId(matchId));
    }

    @GetMapping("/my-playdates")
    public ResponseEntity<List<PlaydateResponseDTO>> getMyPlaydates(Principal principal) {

        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        UserEntity user = userRepository.findByEmail(principal.getName())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        List<PlaydateResponseDTO> playdates = playdateService.findAllByUser(user.getId());

        return ResponseEntity.ok(playdates);
    }
    @PatchMapping("/{playdateId}/confirm")
    @PreAuthorize("hasRole('FAMILY')")
    public ResponseEntity<PlaydateResponseDTO> confirmPlaydate(@PathVariable Long playdateId) {
        return ResponseEntity.ok(playdateService.confirm(playdateId));
    }
}