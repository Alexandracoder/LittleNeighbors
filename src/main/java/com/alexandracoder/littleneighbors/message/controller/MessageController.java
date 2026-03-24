package com.alexandracoder.littleneighbors.message.controller;

import com.alexandracoder.littleneighbors.message.dto.MessageResponseDTO;
import com.alexandracoder.littleneighbors.message.dto.SendMessageDTO;
import com.alexandracoder.littleneighbors.message.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @PostMapping("/send")
    public ResponseEntity<MessageResponseDTO> sendMessage(
            @RequestBody SendMessageDTO dto,
            @AuthenticationPrincipal UserDetails userDetails) {

        return ResponseEntity.ok(messageService.sendMessage(dto, userDetails.getUsername()));
    }

    @GetMapping("/history/{matchId}")
    public ResponseEntity<List<MessageResponseDTO>> getChatHistory(@PathVariable Long matchId) {
        return ResponseEntity.ok(messageService.getChatHistory(matchId));
    }
}