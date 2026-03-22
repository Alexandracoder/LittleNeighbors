package com.alexandracoder.littleneighbors.message.dto;

import com.alexandracoder.littleneighbors.message.entity.MessageEntity;
import org.springframework.stereotype.Component;

@Component
public class MessageMapper {

    public MessageResponseDTO toResponseDTO(MessageEntity entity) {
        return new MessageResponseDTO(
                entity.getId(),
                entity.getContent(),
                entity.getSender().getId(),
                entity.getSender().getFirstName(),
                entity.getSentAt()
        );
    }
}