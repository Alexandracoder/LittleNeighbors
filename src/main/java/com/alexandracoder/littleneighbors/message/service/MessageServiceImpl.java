package com.alexandracoder.littleneighbors.message.service;

import com.alexandracoder.littleneighbors.match.entity.MatchEntity;
import com.alexandracoder.littleneighbors.match.repository.MatchRepository;
import com.alexandracoder.littleneighbors.message.dto.MessageResponseDTO;
import com.alexandracoder.littleneighbors.message.dto.SendMessageDTO;
import com.alexandracoder.littleneighbors.message.entity.MessageEntity;
import com.alexandracoder.littleneighbors.message.dto.MessageMapper;
import com.alexandracoder.littleneighbors.message.repository.MessageRepository;
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
    public MessageResponseDTO sendMessage(SendMessageDTO dto) {
        MatchEntity match = matchRepository.findById(dto.matchId())
                .orElseThrow(() -> new RuntimeException("Match no encontrado"));

        UserEntity sender = userRepository.findById(dto.senderId())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

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
    public List<MessageResponseDTO> getChatHistory(Long matchId) {
        List<MessageEntity> messages = messageRepository.findByMatchIdOrderBySentAtAsc(matchId);

        return messages.stream()
                .map(messageMapper::toResponseDTO)
                .toList();
    }
}