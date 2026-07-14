package com.alexandracoder.littleneighbors.playdate.entity;

import com.alexandracoder.littleneighbors.enums.PlaydateStatus;
import com.alexandracoder.littleneighbors.match.entity.MatchEntity;
import com.alexandracoder.littleneighbors.shared.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "playdates")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@lombok.experimental.SuperBuilder
@EqualsAndHashCode(callSuper = false)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class PlaydateEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "match_id")
    @JsonIgnoreProperties("playdates")
    private MatchEntity match;

    // Familia que propuso la quedada. Necesario para no permitir que la
    // misma familia que la crea sea también quien la confirme (ver
    // V10__add_playdate_creator.sql).
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_family_id")
    private com.alexandracoder.littleneighbors.family.entity.FamilyEntity createdByFamily;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private PlaydateStatus status = PlaydateStatus.PENDING;
}