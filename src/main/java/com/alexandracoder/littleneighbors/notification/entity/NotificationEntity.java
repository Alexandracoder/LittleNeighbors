package com.alexandracoder.littleneighbors.notification.entity;

import com.alexandracoder.littleneighbors.enums.NotificationType;
import com.alexandracoder.littleneighbors.family.entity.FamilyEntity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public final class NotificationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_family_id", nullable = false)
    private FamilyEntity recipientFamily;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 500)
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type = NotificationType.SYSTEM;

    private Long relatedId;

    @Builder.Default
    private boolean isRead = false;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @PrePersist
    protected void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
    }
}