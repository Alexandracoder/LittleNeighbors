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
    public ResponseEntity<PlaydateResponseDTO> createPlaydate(
            @RequestBody PlaydateRequestDTO dto,
            Principal principal) {

        PlaydateEntity newPlaydate = playdateService.createPlaydate(dto, principal.getName());

        return ResponseEntity.ok(mapToResponseDTO(newPlaydate));
    }

    @GetMapping("/match/{matchId}")
    public ResponseEntity<List<PlaydateResponseDTO>> getPlaydatesByMatch(@PathVariable Long matchId) {

        List<PlaydateEntity> playdateEntities = playdateService.findByMatchId(matchId);

        List<PlaydateResponseDTO> response = playdateEntities.stream()
                .map(this::mapToResponseDTO)
                .toList();

        return ResponseEntity.ok(response);
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

    private PlaydateResponseDTO mapToResponseDTO(PlaydateEntity entity) {
        Long matchId = (entity.getMatch() != null) ? entity.getMatch().getId() : null;

        String reqName = "Family Not Found";
        String resName = "Family Not Found";

        if (entity.getMatch() != null) {

            if (entity.getMatch().getChildRequest() != null && entity.getMatch().getChildRequest().getFamily() != null) {
                reqName = entity.getMatch().getChildRequest().getFamily().getFamilyName();
            }
            if (entity.getMatch().getChildTarget() != null && entity.getMatch().getChildTarget().getFamily() != null) {
                resName = entity.getMatch().getChildTarget().getFamily().getFamilyName();
            }
        }

        return new PlaydateResponseDTO(
                entity.getId(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getStartTime(),
                entity.getStatus().name(),
                matchId,
                reqName,
                resName
        );
    }
}