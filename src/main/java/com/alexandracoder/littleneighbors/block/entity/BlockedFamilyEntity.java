package com.alexandracoder.littleneighbors.block.entity;

import com.alexandracoder.littleneighbors.family.entity.FamilyEntity;
import com.alexandracoder.littleneighbors.shared.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "blocked_families")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@lombok.experimental.SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class BlockedFamilyEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "blocker_family_id", nullable = false)
    private FamilyEntity blockerFamily;

    @ManyToOne(optional = false)
    @JoinColumn(name = "blocked_family_id", nullable = false)
    private FamilyEntity blockedFamily;
}
