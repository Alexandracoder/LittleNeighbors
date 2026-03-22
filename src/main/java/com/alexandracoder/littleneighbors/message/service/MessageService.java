package com.alexandracoder.littleneighbors.message.service;

import com.alexandracoder.littleneighbors.message.dto.MessageResponseDTO;
import com.alexandracoder.littleneighbors.message.dto.SendMessageDTO;
import java.util.List;

public interface MessageService {

    // Al enviar, también devolvemos el DTO para que el Front confirme el envío
    MessageResponseDTO sendMessage(SendMessageDTO dto);

    // El historial ahora devuelve la lista de DTOs mapeados
    List<MessageResponseDTO> getChatHistory(Long matchId);
}