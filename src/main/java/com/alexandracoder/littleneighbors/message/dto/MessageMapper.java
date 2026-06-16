package com.alexandracoder.littleneighbors.message.dto;

import com.alexandracoder.littleneighbors.message.entity.MessageEntity;
import org.springframework.stereotype.Component;

@Component
public class MessageMapper {

    public MessageResponseDTO toResponseDTO(MessageEntity entity) {
        return new MessageResponseDTO(
                entity.getId(),
                entity.getSender().getId(),
                entity.getSender().getEmail(),
                entity.getReceiver().getId(),
                entity.getMatch() != null ? entity.getMatch().getId() : null,
                entity.getContent(),
                entity.getSentAt()
        );
    }
}