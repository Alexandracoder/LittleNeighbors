package com.alexandracoder.littleneighbors.message.dto;

import java.time.LocalDateTime;

public record MessageResponseDTO(
        Long id,
        String content,
        Long senderId,
        String senderFirstName, // Para poner "Ana: ¡Hola!" en el chat
        LocalDateTime sentAt
) {}
