package com.alexandracoder.littleneighbors.message.service;

import com.alexandracoder.littleneighbors.match.entity.MatchEntity;
import com.alexandracoder.littleneighbors.match.repository.MatchRepository;
import com.alexandracoder.littleneighbors.message.dto.MessageResponseDTO;
import com.alexandracoder.littleneighbors.message.dto.SendMessageDTO;
import com.alexandracoder.littleneighbors.message.entity.MessageEntity;
import com.alexandracoder.littleneighbors.message.dto.MessageMapper;
import com.alexandracoder.littleneighbors.message.repository.MessageRepository;
import com.alexandracoder.littleneighbors.specifications.MessageSpecifications;
import com.alexandracoder.littleneighbors.shared.exceptions.ResourceNotFoundException;
import com.alexandracoder.littleneighbors.user.entity.UserEntity;
import com.alexandracoder.littleneighbors.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
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
    private final MessageMapper messageMapper;

    @Override
    @Transactional
    public MessageResponseDTO sendMessage(SendMessageDTO dto, String senderEmail) {
        UserEntity sender = userRepository.findByEmail(senderEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        UserEntity receiver = userRepository.findById(dto.receiverId())
                .orElseThrow(() -> new ResourceNotFoundException("Receiver not found"));

        MatchEntity match = null;
        if (dto.matchId() != null) {
            match = matchRepository.findById(dto.matchId())
                    .orElseThrow(() -> new ResourceNotFoundException("Match not found"));
        }

        MessageEntity message = MessageEntity.builder()
                .sender(sender)
                .receiver(receiver)
                .match(match)
                .content(dto.content())
                .sentAt(LocalDateTime.now())
                .build();

        MessageEntity savedMessage = messageRepository.save(message);
        return messageMapper.toResponseDTO(savedMessage);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MessageResponseDTO> getChatHistory(Long myFamilyId, Long matchFamilyId) {
        Specification<MessageEntity> spec =
                MessageSpecifications.isConversationBetween(myFamilyId, matchFamilyId);

        List<MessageEntity> messages = messageRepository.findAll(spec);


        return messages.stream()
                .map(messageMapper::toResponseDTO)
                .toList();
    }
}