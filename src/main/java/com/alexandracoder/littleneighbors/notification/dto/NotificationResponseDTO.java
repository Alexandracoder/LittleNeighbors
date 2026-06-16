package com.alexandracoder.littleneighbors.notification.dto;

import com.alexandracoder.littleneighbors.enums.NotificationType;

import java.time.LocalDateTime;
public record NotificationResponseDTO(
        Long id,
        String title,
        String message,
        NotificationType type,
        Long relatedId,
        boolean isRead,
        LocalDateTime createdAt
) {}

