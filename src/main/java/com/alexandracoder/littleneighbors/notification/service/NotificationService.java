package com.alexandracoder.littleneighbors.notification.service;

import com.alexandracoder.littleneighbors.enums.NotificationType;
import com.alexandracoder.littleneighbors.family.entity.FamilyEntity;
import com.alexandracoder.littleneighbors.match.entity.MatchEntity;
import com.alexandracoder.littleneighbors.notification.entity.NotificationEntity;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface NotificationService {
    void sendMatchSuccessNotification(MatchEntity match);

    List<NotificationEntity> getNotificationsForFamily(Long familyId, Boolean onlyUnread);


    List<NotificationEntity> getNotificationsByUserEmail(String email);

    void markAsRead(Long notificationId);

    @Transactional
    void createInternalNotification(FamilyEntity recipient, String title, String message, NotificationType type, Long targetId);
}