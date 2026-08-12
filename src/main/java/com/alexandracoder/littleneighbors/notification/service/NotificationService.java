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

    // Antes recibía title/message ya escritos (de ahí la mezcla de
    // idiomas: cada sitio los escribía a mano en el idioma que le
    // apeteciera). Ahora solo se pasa el tipo + los datos variables, y el
    // texto se construye en el frontend con i18next en el idioma activo
    // de quien lo recibe.
    @Transactional
    void createInternalNotification(FamilyEntity recipient, NotificationType type, Long relatedId, String param1, String param2);

    // Atajo para el caso frecuente de un solo parámetro.
    @Transactional
    default void createInternalNotification(FamilyEntity recipient, NotificationType type, Long relatedId, String param1) {
        createInternalNotification(recipient, type, relatedId, param1, null);
    }
}