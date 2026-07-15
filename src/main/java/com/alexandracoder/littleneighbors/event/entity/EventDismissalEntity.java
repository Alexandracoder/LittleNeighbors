package com.alexandracoder.littleneighbors.event.entity;

import com.alexandracoder.littleneighbors.family.entity.FamilyEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

// Un registro aquí significa "esta familia ya no quiere ver este evento
// en su lista". No borra el evento real, solo lo oculta para ella.
@Entity
@Table(name = "event_dismissals", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"event_id", "family_id"})
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventDismissalEntity {
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
