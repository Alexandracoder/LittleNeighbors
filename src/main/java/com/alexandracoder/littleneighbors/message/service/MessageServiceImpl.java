package com.alexandracoder.littleneighbors.message.service;

import com.alexandracoder.littleneighbors.family.repository.FamilyRepository;
import com.alexandracoder.littleneighbors.match.entity.MatchEntity;
import com.alexandracoder.littleneighbors.match.repository.MatchRepository;
import com.alexandracoder.littleneighbors.message.dto.MessageResponseDTO;
import com.alexandracoder.littleneighbors.message.dto.SendMessageDTO;
import com.alexandracoder.littleneighbors.message.entity.MessageEntity;
import com.alexandracoder.littleneighbors.message.dto.MessageMapper;
import com.alexandracoder.littleneighbors.message.repository.MessageRepository;
import com.alexandracoder.littleneighbors.shared.exceptions.ResourceNotFoundException;
import com.alexandracoder.littleneighbors.specifications.MessageSpecifications;
import com.alexandracoder.littleneighbors.user.entity.UserEntity;
import com.alexandracoder.littleneighbors.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final MatchRepository matchRepository;
    private final UserRepository userRepository;
    private final FamilyRepository familyRepository;
    private final MessageMapper messageMapper;

    @Override
    @Transactional
    public MessageResponseDTO sendMessage(SendMessageDTO dto, String senderEmail) {
        UserEntity sender = userRepository.findByEmail(senderEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        MatchEntity match = matchRepository.findById(dto.matchId())
                .orElseThrow(() -> new ResourceNotFoundException("Match not found"));

        UserEntity receiver;
        if (match.getChildRequest().getFamily().getUser().getId().equals(sender.getId())) {
            receiver = match.getChildTarget().getFamily().getUser();
        } else {
            receiver = match.getChildRequest().getFamily().getUser();
        }

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
    public List<MessageResponseDTO> getChatHistoryByMatch(Long matchId) {
        Specification<MessageEntity> spec = MessageSpecifications.hasMatchId(matchId);
        return messageRepository.findAll(spec).stream()
                .map(messageMapper::toResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<MessageResponseDTO> getChatHistory(Long familyIdA, Long familyIdB) {
        Long userIdA = familyRepository.findById(familyIdA)
                .map(f -> f.getUser().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Family not found: " + familyIdA));

        Long userIdB = familyRepository.findById(familyIdB)
                .map(f -> f.getUser().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Family not found: " + familyIdB));

        Specification<MessageEntity> spec = MessageSpecifications.isConversationBetween(userIdA, userIdB);

        return messageRepository.findAll(spec).stream()
                .map(messageMapper::toResponseDTO)
                .toList();
    }
}