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

    // Ya no se rellenan en notificaciones nuevas (ver param1/param2 más
    // abajo) — se dejan nullable solo para poder seguir mostrando las
    // notificaciones antiguas ya guardadas antes de este cambio.
    private String title;

    @Column(length = 500)
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType type = NotificationType.SYSTEM;

    private Long relatedId;

    // Datos variables para construir el texto en el idioma activo del
    // destinatario (ver traducciones notifications.types.* en el
    // frontend). P.ej. para EVENT_CREATED: param1 = nombre de quien creó
    // el evento, param2 = título del evento.
    private String param1;
    private String param2;

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