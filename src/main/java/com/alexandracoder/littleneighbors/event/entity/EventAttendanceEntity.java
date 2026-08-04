package com.alexandracoder.littleneighbors.event.entity;

import com.alexandracoder.littleneighbors.family.entity.FamilyEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

// Un registro aquí significa "esta familia se ha apuntado a este evento".
// Es el opuesto de EventDismissalEntity (que oculta un evento sin más).
@Entity
@Table(name = "event_attendances", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"event_id", "family_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventAttendanceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "event_id")
    private EventEntity event;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "family_id")
    private FamilyEntity family;

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
