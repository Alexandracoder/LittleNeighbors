package com.alexandracoder.littleneighbors.notification.service;

import com.alexandracoder.littleneighbors.match.entity.MatchEntity;
import com.alexandracoder.littleneighbors.notification.entity.NotificationEntity;
import java.util.List;

public interface NotificationService {
    void sendMatchSuccessNotification(MatchEntity match);
    List<NotificationEntity> getNotificationsForFamily(Long familyId, Boolean onlyUnread);
    void markAsRead(Long notificationId);
}
