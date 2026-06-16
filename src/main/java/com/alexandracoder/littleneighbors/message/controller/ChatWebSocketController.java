package com.alexandracoder.littleneighbors.message.controller;

import com.alexandracoder.littleneighbors.message.dto.MessageResponseDTO;
import com.alexandracoder.littleneighbors.message.dto.SendMessageDTO;
import com.alexandracoder.littleneighbors.message.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {

    private final MessageService messageService;
    private final SimpMessagingTemplate messagingTemplate;


    @MessageMapping("/chat/{matchId}")
    public void processMessage(@DestinationVariable Long matchId, SendMessageDTO dto, Principal principal) {

        MessageResponseDTO savedMessage = messageService.sendMessage(dto, principal.getName());

        messagingTemplate.convertAndSend("/topic/messages/" + matchId, savedMessage);
    }
}