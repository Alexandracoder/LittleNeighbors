package com.alexandracoder.littleneighbors.notification.service;

import com.alexandracoder.littleneighbors.enums.NotificationType;
import com.alexandracoder.littleneighbors.family.entity.FamilyEntity;
import com.alexandracoder.littleneighbors.match.entity.MatchEntity;
import com.alexandracoder.littleneighbors.notification.dto.NotificationResponseDTO;
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
    private final com.alexandracoder.littleneighbors.family.repository.FamilyRepository familyRepository;
    private final com.alexandracoder.littleneighbors.user.repository.UserRepository userRepository;

    @Override
    @Transactional
    public void sendMatchSuccessNotification(MatchEntity match) {
        createAndSave(match.getChildRequest().getFamily(), match);
        createAndSave(match.getChildTarget().getFamily(), match);
    }

    private void createAndSave(FamilyEntity recipient, MatchEntity match) {
        String email = recipient.getUser().getEmail();
        String otherFamilyName = recipient.getId().equals(match.getChildRequest().getFamily().getId())
                ? match.getChildTarget().getFamily().getFamilyName()
                : match.getChildRequest().getFamily().getFamilyName();

        NotificationEntity notification = NotificationEntity.builder()
                .recipientFamily(recipient)
                .title("Match Established!")
                .message("You can now contact the " + otherFamilyName + " family.")
                .type(NotificationType.MATCH_SUCCESS)
                .relatedId(match.getId())
                .isRead(false)
                .createdAt(LocalDateTime.now())
                .build();

        NotificationEntity saved = notificationRepository.save(notification);
        messagingTemplate.convertAndSendToUser(email, "/queue/notifications", mapToResponseDTO(saved));
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponseDTO> getNotificationsForFamily(Long familyId, Boolean onlyUnread, String currentUserEmail) {
        assertOwnsFamilyOrAdmin(familyId, currentUserEmail);

        Specification<NotificationEntity> spec = Specification.where(NotificationSpecifications.hasRecipientFamily(familyId));

        if (Boolean.TRUE.equals(onlyUnread)) {
            spec = spec.and(NotificationSpecifications.isUnread());
        }

        return notificationRepository.findAll(spec).stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    @Override
    @Transactional
    public void markAsRead(Long notificationId, String currentUserEmail) {
        NotificationEntity notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));

        assertOwnsFamilyOrAdmin(notification.getRecipientFamily().getId(), currentUserEmail);

        notification.setRead(true);
        notificationRepository.save(notification);
    }

    private void assertOwnsFamilyOrAdmin(Long familyId, String currentUserEmail) {
        com.alexandracoder.littleneighbors.user.entity.UserEntity currentUser = userRepository.findByEmail(currentUserEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        boolean isAdmin = currentUser.getRoles().stream().anyMatch(role -> role.name().equals("ADMIN"));
        if (isAdmin) return;

        com.alexandracoder.littleneighbors.family.entity.FamilyEntity myFamily = familyRepository.findByUserEmail(currentUserEmail)
                .orElseThrow(() -> new org.springframework.security.access.AccessDeniedException("You don't have a family profile"));

        if (!myFamily.getId().equals(familyId)) {
            throw new org.springframework.security.access.AccessDeniedException("You can't access another family's notifications");
        }
    }

    @Override
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
        NotificationResponseDTO dto = mapToResponseDTO(savedNotification);

        try {
            String userEmail = neighbor.getUser().getEmail();
            messagingTemplate.convertAndSendToUser(userEmail, "/queue/notifications", dto);
        } catch (Exception e) {
            System.err.println("Error enviando WebSocket: " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<NotificationResponseDTO> getNotificationsByUserEmail(String email) {
        return notificationRepository.findByRecipientFamily_User_EmailOrderByCreatedAtDesc(email)
                .stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    private NotificationResponseDTO mapToResponseDTO(NotificationEntity entity) {
        return new NotificationResponseDTO(
                entity.getId(),
                entity.getTitle(),
                entity.getMessage(),
                entity.getType(),
                entity.getRelatedId(),
                entity.isRead(),
                entity.getCreatedAt()
        );
    }
}