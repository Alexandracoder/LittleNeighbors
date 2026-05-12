package com.alexandracoder.littleneighbors.notification.service;

import com.alexandracoder.littleneighbors.enums.NotificationType;
import com.alexandracoder.littleneighbors.family.entity.FamilyEntity;
import com.alexandracoder.littleneighbors.match.entity.MatchEntity;
import com.alexandracoder.littleneighbors.notification.entity.NotificationEntity;
import com.alexandracoder.littleneighbors.notification.repository.NotificationRepository;
import com.alexandracoder.littleneighbors.specifications.NotificationSpecifications;
import com.alexandracoder.littleneighbors.shared.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Override
    @Transactional
    public void sendMatchSuccessNotification(MatchEntity match) {
        createAndSave(match.getChildRequest().getFamily(), match);
        createAndSave(match.getChildTarget().getFamily(), match);
    }

    private void createAndSave(FamilyEntity recipient, MatchEntity match) {
        String otherFamilyName = recipient.getId().equals(match.getChildRequest().getFamily().getId())
                ? match.getChildTarget().getFamily().getFamilyName()
                : match.getChildRequest().getFamily().getFamilyName();

        NotificationEntity notification = NotificationEntity.builder()
                .recipientFamily(recipient)
                .title("Match Established!")
                .message("You can now contact the " + otherFamilyName + " family to organize a playdate.")
                .relatedId(match.getId())
                .build();

        notificationRepository.save(notification);


        messagingTemplate.convertAndSend("/topic/playdates/" + match.getId(), "NOTIFICATION_RECEIVED");
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationEntity> getNotificationsForFamily(Long familyId, Boolean onlyUnread) {
        Specification<NotificationEntity> spec = Specification.where(NotificationSpecifications.hasRecipientFamily(familyId));

        if (Boolean.TRUE.equals(onlyUnread)) {
            spec = spec.and(NotificationSpecifications.isUnread());
        }

        return notificationRepository.findAll(spec);
    }

    @Override
    @Transactional
    public void markAsRead(Long notificationId) {
        NotificationEntity notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));
        notification.setRead(true);
        notificationRepository.save(notification);
    }

    @Transactional
    public void createInternalNotification(FamilyEntity neighbor, String title, String message, NotificationType type, Long relatedId) {
        NotificationEntity notification = NotificationEntity.builder()
                .recipientFamily(neighbor)
                .title(title)
                .message(message)
                .type(type)
                .relatedId(relatedId)
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();

        NotificationEntity savedNotification = notificationRepository.save(notification);

        try {

            String userEmail = neighbor.getUser().getEmail();

            messagingTemplate.convertAndSendToUser(
                    userEmail,
                    "/queue/notifications",
                    savedNotification
            );
        } catch (Exception e) {
            System.err.println("Error enviando WebSocket: " + e.getMessage());
        }
    }

    public List<NotificationEntity> getNotificationsByUserEmail(String email) {
        return notificationRepository.findByRecipientFamily_User_EmailOrderByCreatedAtDesc(email);
    }
}
