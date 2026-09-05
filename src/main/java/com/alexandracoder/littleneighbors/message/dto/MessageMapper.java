package com.alexandracoder.littleneighbors.message.dto;

import com.alexandracoder.littleneighbors.message.entity.MessageEntity;
import org.springframework.stereotype.Component;

@Component
public class MessageMapper {

    public MessageResponseDTO toResponseDTO(MessageEntity entity) {
        // sender/receiver pueden ser null si esa persona ha eliminado su
        // cuenta (V22: ON DELETE SET NULL) — el mensaje se conserva para
        // quien sigue teniendo cuenta, solo se pierde la referencia a
        // quien se borró.
        return new MessageResponseDTO(
                entity.getId(),
                entity.getSender() != null ? entity.getSender().getId() : null,
                entity.getSender() != null ? entity.getSender().getEmail() : null,
                entity.getReceiver() != null ? entity.getReceiver().getId() : null,
                entity.getMatch() != null ? entity.getMatch().getId() : null,
                entity.getContent(),
                entity.getSentAt()
        );
    }
}