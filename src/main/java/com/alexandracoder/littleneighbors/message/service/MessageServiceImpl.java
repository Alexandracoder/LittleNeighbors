package com.alexandracoder.littleneighbors.message.service;

import com.alexandracoder.littleneighbors.family.repository.FamilyRepository;
import com.alexandracoder.littleneighbors.match.entity.MatchEntity;
import com.alexandracoder.littleneighbors.match.repository.MatchRepository;
import com.alexandracoder.littleneighbors.message.dto.*;
import com.alexandracoder.littleneighbors.message.entity.MessageEntity;
import com.alexandracoder.littleneighbors.message.repository.MessageRepository;
import com.alexandracoder.littleneighbors.shared.exceptions.ResourceNotFoundException;
import com.alexandracoder.littleneighbors.specifications.MessageSpecifications;
import com.alexandracoder.littleneighbors.user.entity.UserEntity;
import com.alexandracoder.littleneighbors.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Transactional
@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final MatchRepository matchRepository;
    private final UserRepository userRepository;
    private final FamilyRepository familyRepository;
    private final MessageMapper messageMapper;

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

        return messageMapper.toResponseDTO(messageRepository.save(message));
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

    @Override
    @Transactional
    public MessageWebSocketDTO saveFromWebSocket(Long matchId, MessageWebSocketDTO dto) {
        MatchEntity match = matchRepository.findById(matchId)
                .orElseThrow(() -> new EntityNotFoundException("Match not found"));

        UserEntity sender = userRepository.findById(dto.senderId())
                .orElseThrow(() -> new EntityNotFoundException("Sender not found"));

        validateUserInMatch(match, sender.getId());

        UserEntity receiver = (match.getRequestUser().getId().equals(sender.getId()))
                ? match.getTargetUser()
                : match.getRequestUser();

        if (receiver == null) {
            throw new RuntimeException("Receiver could not be determined");
        }

        MessageEntity entity = MessageEntity.builder()
                .match(match)
                .sender(sender)
                .receiver(receiver)
                .content(dto.content())
                .sentAt(LocalDateTime.now())
                .build();

        MessageEntity saved = messageRepository.save(entity);

        return new MessageWebSocketDTO(
                saved.getId(),
                match.getId(),
                sender.getId(),
                sender.getFullName(),
                saved.getContent(),
                saved.getSentAt()
        );
    }
}