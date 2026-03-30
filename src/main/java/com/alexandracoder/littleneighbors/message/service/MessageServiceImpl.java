package com.alexandracoder.littleneighbors.message.service;

import com.alexandracoder.littleneighbors.match.entity.MatchEntity;
import com.alexandracoder.littleneighbors.match.repository.MatchRepository;
import com.alexandracoder.littleneighbors.message.dto.MessageResponseDTO;
import com.alexandracoder.littleneighbors.message.dto.SendMessageDTO;
import com.alexandracoder.littleneighbors.message.entity.MessageEntity;
import com.alexandracoder.littleneighbors.message.dto.MessageMapper;
import com.alexandracoder.littleneighbors.message.repository.MessageRepository;
import com.alexandracoder.littleneighbors.shared.exceptions.ResourceNotFoundException;
import com.alexandracoder.littleneighbors.shared.exceptions.UnauthorizedAccessException;
import com.alexandracoder.littleneighbors.user.entity.UserEntity;
import com.alexandracoder.littleneighbors.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
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
    private final MessageMapper messageMapper;

    @Override
    @Transactional
    public MessageResponseDTO sendMessage(SendMessageDTO dto, String senderEmail) {
        UserEntity sender = userRepository.findByEmail(senderEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        MatchEntity match = matchRepository.findById(dto.matchId())
                .orElseThrow(() -> new ResourceNotFoundException("Match not found"));

        boolean isParticipant = match.getChildA().getFamily().getUser().getId().equals(sender.getId()) ||
                match.getChildB().getFamily().getUser().getId().equals(sender.getId());

        if (!isParticipant) {
            throw new UnauthorizedAccessException("You are not a participant in this conversation.");
        }

        MessageEntity message = MessageEntity.builder()
                .match(match)
                .sender(sender)
                .content(dto.content())
                .sentAt(LocalDateTime.now())
                .build();

        MessageEntity savedMessage = messageRepository.save(message);
        return messageMapper.toResponseDTO(savedMessage);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MessageResponseDTO> getChatHistory(Long matchId) {
        if (!matchRepository.existsById(matchId)) {
            throw new ResourceNotFoundException("Match not found with ID: " + matchId);
        }

        List<MessageEntity> messages = messageRepository.findByMatchIdOrderBySentAtAsc(matchId);

        return messages.stream()
                .map(messageMapper::toResponseDTO)
                .toList();
    }
}