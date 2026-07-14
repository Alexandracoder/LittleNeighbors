package com.alexandracoder.littleneighbors.message.service;

import com.alexandracoder.littleneighbors.family.repository.FamilyRepository;
import com.alexandracoder.littleneighbors.match.entity.MatchEntity;
import com.alexandracoder.littleneighbors.match.repository.MatchRepository;
import com.alexandracoder.littleneighbors.message.dto.*;
import com.alexandracoder.littleneighbors.message.entity.MessageEntity;
import com.alexandracoder.littleneighbors.message.repository.MessageRepository;
import com.alexandracoder.littleneighbors.enums.NotificationType;
import com.alexandracoder.littleneighbors.notification.service.NotificationService;
import com.alexandracoder.littleneighbors.shared.exceptions.ResourceNotFoundException;
import com.alexandracoder.littleneighbors.specifications.MessageSpecifications;
import com.alexandracoder.littleneighbors.user.entity.UserEntity;
import com.alexandracoder.littleneighbors.user.repository.UserRepository;
import com.alexandracoder.littleneighbors.block.service.BlockService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Transactional
@Service
@RequiredArgsConstructor
@Slf4j
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final MatchRepository matchRepository;
    private final UserRepository userRepository;
    private final FamilyRepository familyRepository;
    private final MessageMapper messageMapper;
    private final NotificationService notificationService;
    private final BlockService blockService;

    private void validateUserInMatch(MatchEntity match, Long currentUserId) {
        Long requesterId = match.getChildRequest().getFamily().getUser().getId();
        Long targetId = match.getChildTarget().getFamily().getUser().getId();

        if (!requesterId.equals(currentUserId) && !targetId.equals(currentUserId)) {
            throw new AccessDeniedException("You are not part of this match");
        }
    }

    @Override
    @Transactional
    public MessageResponseDTO sendMessage(SendMessageDTO dto, String senderEmail) {
        UserEntity sender = userRepository.findByEmail(senderEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        MatchEntity match = matchRepository.findById(dto.matchId())
                .orElseThrow(() -> new ResourceNotFoundException("Match not found"));

        validateUserInMatch(match, sender.getId());

        Long senderFamilyId = match.getChildRequest().getFamily().getUser().getId().equals(sender.getId())
                ? match.getChildRequest().getFamily().getId()
                : match.getChildTarget().getFamily().getId();
        Long otherFamilyId = senderFamilyId.equals(match.getChildRequest().getFamily().getId())
                ? match.getChildTarget().getFamily().getId()
                : match.getChildRequest().getFamily().getId();

        if (blockService.isBlockedEitherWay(senderFamilyId, otherFamilyId)) {
            log.warn("Mensaje rechazado: bloqueo activo entre familias {} y {} (match {})",
                    senderFamilyId, otherFamilyId, match.getId());
            throw new AccessDeniedException("Messaging is not available for this conversation.");
        }

        UserEntity receiver = (match.getChildRequest().getFamily().getUser().getId().equals(sender.getId()))
                ? match.getChildTarget().getFamily().getUser()
                : match.getChildRequest().getFamily().getUser();

        MessageEntity message = MessageEntity.builder()
                .sender(sender)
                .receiver(receiver)
                .match(match)
                .content(dto.content())
                .sentAt(LocalDateTime.now())
                .build();

        MessageEntity savedMessage = messageRepository.save(message);

        // Antes el mensaje de la notificación incluía el contenido real del
        // chat (truncado). Por privacidad, mejor solo avisar de que hay
        // mensaje nuevo, sin mostrar qué dice hasta que se abre el chat.
        notificationService.createInternalNotification(
                receiver.getFamily(),
                "Nuevo mensaje",
                "Tienes un mensaje nuevo de " + sender.getFullName(),
                NotificationType.CHAT_MESSAGE,
                match.getId()
        );

        return messageMapper.toResponseDTO(savedMessage);
    }

    private String truncate(String text) {
        if (text == null) return "";
        return text.length() > 60 ? text.substring(0, 57) + "..." : text;
    }

    @Override
    @Transactional(readOnly = true)
    public ChatHistoryResponseDTO getChatHistoryByMatch(Long matchId, String currentUserEmail) {
        UserEntity currentUser = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        MatchEntity match = matchRepository.findById(matchId)
                .orElseThrow(() -> new ResourceNotFoundException("Match not found"));

        validateUserInMatch(match, currentUser.getId());

        Specification<MessageEntity> spec = MessageSpecifications.hasMatchId(matchId);
        List<MessageResponseDTO> messages = messageRepository.findAll(spec).stream()
                .map(messageMapper::toResponseDTO)
                .toList();

        boolean isRequester = match.getChildRequest().getFamily().getUser().getEmail().equals(currentUserEmail);
        boolean userAccepted = isRequester ? match.isUserAccepted() : match.isNeighborAccepted();
        boolean neighborAccepted = isRequester ? match.isNeighborAccepted() : match.isUserAccepted();

        return new ChatHistoryResponseDTO(messages, userAccepted, neighborAccepted);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MessageResponseDTO> getChatHistory(Long familyIdA, Long familyIdB) {
        throw new UnsupportedOperationException("Use getChatHistoryByMatch instead for security reasons");
    }

}