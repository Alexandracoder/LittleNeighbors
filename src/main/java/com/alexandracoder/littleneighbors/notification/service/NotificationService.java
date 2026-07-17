package com.alexandracoder.littleneighbors.notification.service;

import com.alexandracoder.littleneighbors.enums.NotificationType;
import com.alexandracoder.littleneighbors.family.entity.FamilyEntity;
import com.alexandracoder.littleneighbors.match.entity.MatchEntity;
import com.alexandracoder.littleneighbors.notification.dto.NotificationResponseDTO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface NotificationService {

    void sendMatchSuccessNotification(MatchEntity match);

    List<NotificationResponseDTO> getNotificationsForFamily(Long familyId, Boolean onlyUnread, String currentUserEmail);


    List<NotificationResponseDTO> getNotificationsByUserEmail(String email);

    void markAsRead(Long notificationId, String currentUserEmail);

    @Transactional
    void createInternalNotification(FamilyEntity recipient, String title, String message, NotificationType type, Long relatedId);
}