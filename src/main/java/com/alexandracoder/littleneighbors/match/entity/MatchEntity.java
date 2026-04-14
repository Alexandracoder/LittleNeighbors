package com.alexandracoder.littleneighbors.match.entity;

import com.alexandracoder.littleneighbors.child.entity.ChildEntity;
import com.alexandracoder.littleneighbors.enums.MatchStatus;
import com.alexandracoder.littleneighbors.shared.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class MatchEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "child_request_id")
    @JsonIgnoreProperties({"matches", "interests", "family"})
    private ChildEntity childRequest;

    @ManyToOne(optional = false)
    @JoinColumn(name = "child_target_id")
    @JsonIgnoreProperties({"matches", "interests", "family"})
    private ChildEntity childTarget;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MatchStatus status;

    @Column(nullable = false)
    @Builder.Default
    private boolean userAccepted = false;

    @Column(nullable = false)
    @Builder.Default
    private boolean neighborAccepted = false;
}