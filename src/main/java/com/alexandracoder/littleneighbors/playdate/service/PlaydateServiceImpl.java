package com.alexandracoder.littleneighbors.playdate.service;

import com.alexandracoder.littleneighbors.child.entity.ChildEntity;
import com.alexandracoder.littleneighbors.enums.PlaydateStatus;
import com.alexandracoder.littleneighbors.match.entity.MatchEntity;
import com.alexandracoder.littleneighbors.match.repository.MatchRepository;
import com.alexandracoder.littleneighbors.playdate.dto.PlaydateRequestDTO;
import com.alexandracoder.littleneighbors.playdate.dto.PlaydateResponseDTO;
import com.alexandracoder.littleneighbors.playdate.entity.PlaydateEntity;
import com.alexandracoder.littleneighbors.playdate.repository.PlaydateRepository;
import com.alexandracoder.littleneighbors.shared.exceptions.ResourceNotFoundException;
import com.alexandracoder.littleneighbors.specifications.PlaydateSpecifications;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PlaydateServiceImpl implements PlaydateService {

    private final PlaydateRepository playdateRepository;
    private final MatchRepository matchRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    @Transactional
    public PlaydateResponseDTO createPlaydate(PlaydateRequestDTO dto, String currentUserEmail) {
        MatchEntity match = matchRepository.findById(dto.matchId())
                .orElseThrow(() -> new ResourceNotFoundException("Match not found with ID: " + dto.matchId()));

        String initiatorEmail = match.getChildRequest().getFamily().getUser().getEmail();
        String targetEmail = match.getChildTarget().getFamily().getUser().getEmail();

        if (!currentUserEmail.equals(initiatorEmail) && !currentUserEmail.equals(targetEmail)) {
            throw new AccessDeniedException("You are not part of this match");
        }

        PlaydateEntity playdate = PlaydateEntity.builder()
                .title(dto.title())
                .startTime(dto.startTime())
                .description(dto.description())
                .match(match)
                .status(PlaydateStatus.PENDING)
                .build();

        PlaydateEntity saved = playdateRepository.save(playdate);

        // El frontend (SchedulesPage.tsx) está suscrito a este topic y
        // simplemente vuelve a pedir la lista en cuanto le llega algo aquí
        // (no le importa el contenido del mensaje, solo que llegue algo).
        // Antes nadie publicaba en este topic, así que la otra familia
        // tenía que refrescar la página a mano para ver la propuesta nueva.
        messagingTemplate.convertAndSend("/topic/playdates/" + match.getId(), "updated");

        return mapToResponseDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlaydateResponseDTO> findByMatchId(Long matchId, String currentUserEmail) {
        MatchEntity match = matchRepository.findById(matchId)
                .orElseThrow(() -> new ResourceNotFoundException("Match not found with ID: " + matchId));

        String requesterEmail = match.getChildRequest().getFamily().getUser().getEmail();
        String targetEmail = match.getChildTarget().getFamily().getUser().getEmail();
        if (!requesterEmail.equals(currentUserEmail) && !targetEmail.equals(currentUserEmail)) {
            throw new AccessDeniedException("You are not part of this match");
        }

        return playdateRepository.findByMatchId(matchId)
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlaydateResponseDTO> findAllByUser(Long userId) {

        return playdateRepository.findAll(PlaydateSpecifications.hasUserInMatch(userId))
                .stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public PlaydateResponseDTO confirm(Long playdateId) {
        PlaydateEntity playdate = playdateRepository.findById(playdateId)
                .orElseThrow(() -> new EntityNotFoundException("Playdate not found with ID: " + playdateId));

        if (playdate.getStatus() != PlaydateStatus.PENDING) {
            throw new IllegalStateException("Only pending playdates can be confirmed");
        }

        playdate.setStatus(PlaydateStatus.ACCEPTED);
        PlaydateEntity updatedPlaydate = playdateRepository.save(playdate);

        messagingTemplate.convertAndSend(
                "/topic/playdates/" + updatedPlaydate.getMatch().getId(), "updated");

        return mapToResponseDTO(updatedPlaydate);
    }

    private PlaydateResponseDTO mapToResponseDTO(PlaydateEntity entity) {

        Long matchId = (entity.getMatch() != null) ? entity.getMatch().getId() : null;


        String reqName = "Family Not Found";
        String resName = "Family Not Found";

        if (entity.getMatch() != null) {

            ChildEntity reqChild = entity.getMatch().getChildRequest();
            if (reqChild != null && reqChild.getFamily() != null) {
                reqName = reqChild.getFamily().getFamilyName();
            }


            ChildEntity resChild = entity.getMatch().getChildTarget();
            if (resChild != null && resChild.getFamily() != null) {
                resName = resChild.getFamily().getFamilyName();
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