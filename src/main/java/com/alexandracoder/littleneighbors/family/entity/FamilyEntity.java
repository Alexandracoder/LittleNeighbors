package com.alexandracoder.littleneighbors.family.entity;

import com.alexandracoder.littleneighbors.child.entity.ChildEntity;
import com.alexandracoder.littleneighbors.enums.FamilyStatus;
import com.alexandracoder.littleneighbors.neighborhood.entity.NeighborhoodEntity;
import com.alexandracoder.littleneighbors.shared.BaseEntity;
import com.alexandracoder.littleneighbors.user.entity.UserEntity;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.BatchSize;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "families")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@lombok.experimental.SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class FamilyEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    @JsonBackReference
    private UserEntity user;

    @Column(name = "representative_name", length = 255)
    private String representativeName;

    @Column(name = "family_name", length = 255)
    private String familyName;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(name = "profile_picture_url", length = 255, nullable = true)
    private String profilePictureUrl;

    @ManyToOne
    @JoinColumn(name = "neighborhood_id" , nullable = false)
    private NeighborhoodEntity neighborhood;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private FamilyStatus status = FamilyStatus.SURPRISE;

    @Builder.Default
    @ElementCollection
    @CollectionTable(name = "family_interests", joinColumns = @JoinColumn(name = "family_id"))
    @Column(name = "interest")
    @BatchSize(size = 20)
    private List<String> familyInterests = new ArrayList<>();

    @ToString.Exclude
    @Builder.Default
    @OneToMany(mappedBy = "family", cascade = CascadeType.ALL, orphanRemoval = true)
    @EqualsAndHashCode.Exclude
    @JsonManagedReference
    @BatchSize(size = 10)

    private List<ChildEntity> children = new ArrayList<>();
}
