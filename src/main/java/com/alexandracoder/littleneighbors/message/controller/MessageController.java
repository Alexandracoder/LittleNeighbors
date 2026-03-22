package com.alexandracoder.littleneighbors.message.controller;

import com.alexandracoder.littleneighbors.message.dto.MessageResponseDTO;
import com.alexandracoder.littleneighbors.message.dto.SendMessageDTO; // <--- El record que ya tienes
import com.alexandracoder.littleneighbors.message.entity.MessageEntity;
import com.alexandracoder.littleneighbors.message.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @PostMapping("/send")
    public ResponseEntity<?> sendMessage(@RequestBody SendMessageDTO dto) {
        try {
            // Pasamos el objeto 'dto' completo al service
            MessageResponseDTO response = messageService.sendMessage(dto);

            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            // Captura errores como "Match no encontrado" o "Usuario no encontrado"
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            // Captura cualquier otro error inesperado
            return ResponseEntity.internalServerError().body("Error al enviar el mensaje: " + e.getMessage());
        }
    }

    @GetMapping("/history/{matchId}")
    public ResponseEntity<List<MessageResponseDTO>> getChatHistory(@PathVariable Long matchId) {
        try {
            // Llamamos al service que ya tiene la lógica del Mapper y el Repo
            List<MessageResponseDTO> history = messageService.getChatHistory(matchId);

            // Si no hay mensajes, devolvemos una lista vacía (200 OK), no un error
            return ResponseEntity.ok(history);
        } catch (Exception e) {
            // Por si el matchId no existe o hay un error de DB
            return ResponseEntity.badRequest().build();
        }
    }
}