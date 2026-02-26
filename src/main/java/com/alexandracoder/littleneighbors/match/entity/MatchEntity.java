package com.alexandracoder.littleneighbors.match.entity;

import com.alexandracoder.littleneighbors.child.entity.ChildEntity;
import com.alexandracoder.littleneighbors.enums.MatchStatus;
import com.alexandracoder.littleneighbors.shared.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "matches")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class MatchEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "child_a_id")
    private ChildEntity childA;

    @ManyToOne(optional = false)
    @JoinColumn(name = "child_b_id")
    private ChildEntity childB;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MatchStatus status;
}