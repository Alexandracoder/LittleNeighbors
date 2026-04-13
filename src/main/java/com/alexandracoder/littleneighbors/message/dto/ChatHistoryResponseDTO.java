package com.alexandracoder.littleneighbors.message.dto;

import java.util.List;

public record ChatHistoryResponseDTO(
        List<MessageResponseDTO> messages,
        boolean userAccepted,
        boolean neighborAccepted
) {}