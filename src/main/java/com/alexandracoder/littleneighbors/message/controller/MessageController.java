package com.alexandracoder.littleneighbors.message.controller;

import com.alexandracoder.littleneighbors.message.dto.MessageResponseDTO;
import com.alexandracoder.littleneighbors.message.dto.SendMessageDTO;
import com.alexandracoder.littleneighbors.message.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
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
            @AuthenticationPrincipal Jwt jwt) {

        if (jwt == null) {
            return ResponseEntity.status(401).build();
        }

        String email = jwt.getSubject();
        return ResponseEntity.ok(messageService.sendMessage(dto, email));
    }

    @GetMapping("/history/{myFamilyId}/{matchFamilyId}")
    public ResponseEntity<List<MessageResponseDTO>> getHistory(
            @PathVariable Long myFamilyId,
            @PathVariable Long matchFamilyId,
            @AuthenticationPrincipal Jwt jwt) {

        if (jwt == null) {
            return ResponseEntity.status(401).build();
        }

        return ResponseEntity.ok(messageService.getChatHistory(myFamilyId, matchFamilyId));
    }
}