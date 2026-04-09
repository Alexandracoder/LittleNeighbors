package com.alexandracoder.littleneighbors.message.service;

import com.alexandracoder.littleneighbors.message.dto.MessageResponseDTO;
import com.alexandracoder.littleneighbors.message.dto.SendMessageDTO;
import java.util.List;

public interface MessageService {

    MessageResponseDTO sendMessage(SendMessageDTO dto, String senderEmail);

    List<MessageResponseDTO> getChatHistory(Long user1Id, Long user2Id);
}