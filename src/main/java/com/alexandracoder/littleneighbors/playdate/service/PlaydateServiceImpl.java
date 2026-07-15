package com.alexandracoder.littleneighbors.playdate.service;

import com.alexandracoder.littleneighbors.child.entity.ChildEntity;
import com.alexandracoder.littleneighbors.enums.NotificationType;
import com.alexandracoder.littleneighbors.enums.PlaydateStatus;
import com.alexandracoder.littleneighbors.family.entity.FamilyEntity;
import com.alexandracoder.littleneighbors.family.repository.FamilyRepository;
import com.alexandracoder.littleneighbors.match.entity.MatchEntity;
import com.alexandracoder.littleneighbors.match.repository.MatchRepository;
import com.alexandracoder.littleneighbors.notification.service.NotificationService;
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
    private final FamilyRepository familyRepository;
    private final NotificationService notificationService;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    @Transactional
    public PlaydateResponseDTO createPlaydate(PlaydateRequestDTO dto, String currentUserEmail) {
        MatchEntity match = matchRepository.findById(dto.matchId())
                .orElseThrow(() -> new ResourceNotFoundException("Match not found with ID: " + dto.matchId()));

        FamilyEntity requesterFamily = match.getChildRequest().getFamily();
        FamilyEntity targetFamily = match.getChildTarget().getFamily();
        String initiatorEmail = requesterFamily.getUser().getEmail();
        String targetEmail = targetFamily.getUser().getEmail();

        if (!currentUserEmail.equals(initiatorEmail) && !currentUserEmail.equals(targetEmail)) {
            throw new AccessDeniedException("You are not part of this match");
        }

        FamilyEntity creatorFamily = currentUserEmail.equals(initiatorEmail) ? requesterFamily : targetFamily;
        FamilyEntity recipientFamily = currentUserEmail.equals(initiatorEmail) ? targetFamily : requesterFamily;

        PlaydateEntity playdate = PlaydateEntity.builder()
                .title(dto.title())
                .startTime(dto.startTime())
                .description(dto.description())
                .match(match)
                .createdByFamily(creatorFamily)
                .status(PlaydateStatus.PENDING)
                .build();

        PlaydateEntity saved = playdateRepository.save(playdate);

        notificationService.createInternalNotification(
                recipientFamily,
                "Nueva propuesta de quedada",
                creatorFamily.getFamilyName() + " os ha propuesto un plan: \"" + dto.title() + "\"",
                NotificationType.PLAYDATE_REQUEST,
                match.getId());

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
    public PlaydateResponseDTO confirm(Long playdateId, String currentUserEmail) {
        PlaydateEntity playdate = playdateRepository.findById(playdateId)
                .orElseThrow(() -> new EntityNotFoundException("Playdate not found with ID: " + playdateId));

        if (playdate.getStatus() != PlaydateStatus.PENDING) {
            throw new IllegalStateException("Only pending playdates can be confirmed");
        }

        FamilyEntity requesterFamily = playdate.getMatch().getChildRequest().getFamily();
        FamilyEntity targetFamily = playdate.getMatch().getChildTarget().getFamily();
        String requesterEmail = requesterFamily.getUser().getEmail();
        String targetEmail = targetFamily.getUser().getEmail();

        if (!requesterEmail.equals(currentUserEmail) && !targetEmail.equals(currentUserEmail)) {
            throw new AccessDeniedException("You are not part of this match");
        }

        if (playdate.getCreatedByFamily() != null
                && currentUserEmail.equals(playdate.getCreatedByFamily().getUser().getEmail())) {
            throw new AccessDeniedException("You can't confirm a playdate you proposed yourself");
        }

        playdate.setStatus(PlaydateStatus.ACCEPTED);
        PlaydateEntity updatedPlaydate = playdateRepository.save(playdate);

        if (updatedPlaydate.getCreatedByFamily() != null) {
            notificationService.createInternalNotification(
                    updatedPlaydate.getCreatedByFamily(),
                    "¡Plan confirmado!",
                    "Vuestra propuesta \"" + updatedPlaydate.getTitle() + "\" ha sido confirmada 🎉",
                    NotificationType.MATCH_CONFIRMED,
                    updatedPlaydate.getMatch().getId());
        }

        messagingTemplate.convertAndSend(
                "/topic/playdates/" + updatedPlaydate.getMatch().getId(), "updated");

        return mapToResponseDTO(updatedPlaydate);
    }

    @Override
    @Transactional
    public PlaydateResponseDTO reject(Long playdateId, String currentUserEmail) {
        PlaydateEntity playdate = playdateRepository.findById(playdateId)
                .orElseThrow(() -> new EntityNotFoundException("Playdate not found with ID: " + playdateId));

        if (playdate.getStatus() != PlaydateStatus.PENDING) {
            throw new IllegalStateException("Only pending playdates can be rejected");
        }

        FamilyEntity requesterFamily = playdate.getMatch().getChildRequest().getFamily();
        FamilyEntity targetFamily = playdate.getMatch().getChildTarget().getFamily();
        String requesterEmail = requesterFamily.getUser().getEmail();
        String targetEmail = targetFamily.getUser().getEmail();

        if (!requesterEmail.equals(currentUserEmail) && !targetEmail.equals(currentUserEmail)) {
            throw new AccessDeniedException("You are not part of this match");
        }

        if (playdate.getCreatedByFamily() != null
                && currentUserEmail.equals(playdate.getCreatedByFamily().getUser().getEmail())) {
            throw new AccessDeniedException("You can't reject a playdate you proposed yourself");
        }

        playdate.setStatus(PlaydateStatus.REJECTED);
        PlaydateEntity updatedPlaydate = playdateRepository.save(playdate);

        if (updatedPlaydate.getCreatedByFamily() != null) {
            notificationService.createInternalNotification(
                    updatedPlaydate.getCreatedByFamily(),
                    "Plan rechazado",
                    "Vuestra propuesta \"" + updatedPlaydate.getTitle() + "\" no ha podido confirmarse esta vez.",
                    NotificationType.PLAYDATE_REJECTED,
                    updatedPlaydate.getMatch().getId());
        }

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
                resName,
                entity.getCreatedByFamily() != null ? entity.getCreatedByFamily().getId() : null
        );
    }
}