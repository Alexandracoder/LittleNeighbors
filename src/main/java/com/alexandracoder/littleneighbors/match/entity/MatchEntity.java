package com.alexandracoder.littleneighbors.match.entity;

import com.alexandracoder.littleneighbors.child.entity.ChildEntity;
import com.alexandracoder.littleneighbors.enums.MatchStatus;
import com.alexandracoder.littleneighbors.shared.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "matches")
@Getter
@Setter
@lombok.experimental.SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class MatchEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "child_request_id")
    private ChildEntity childRequest;

    @ManyToOne(optional = false)
    @JoinColumn(name = "child_target_id")
    private ChildEntity childTarget;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MatchStatus status;

    @Column(nullable = false)
    @Builder.Default
    private boolean userAccepted = false; // Aceptación de la familia que inició (Request)

    @Column(nullable = false)
    @Builder.Default
    private boolean neighborAccepted = false; // Aceptación de la familia receptora (Target)
}