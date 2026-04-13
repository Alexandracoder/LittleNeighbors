package com.alexandracoder.littleneighbors.message.service;

import com.alexandracoder.littleneighbors.message.dto.ChatHistoryResponseDTO;
import com.alexandracoder.littleneighbors.message.dto.MessageResponseDTO;
import com.alexandracoder.littleneighbors.message.dto.SendMessageDTO;
import com.alexandracoder.littleneighbors.message.entity.MessageEntity;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface MessageService {

    MessageResponseDTO sendMessage(SendMessageDTO dto, String senderEmail);

    @Transactional(readOnly = true)
    List<MessageResponseDTO> getChatHistory(Long myFamilyId, Long matchFamilyId);

    ChatHistoryResponseDTO getChatHistoryByMatch(Long matchId, String currentUserEmail);

}