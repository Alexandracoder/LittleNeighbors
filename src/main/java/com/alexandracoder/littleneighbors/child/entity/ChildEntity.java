package com.alexandracoder.littleneighbors.child.entity;

import com.alexandracoder.littleneighbors.enums.Gender;
import com.alexandracoder.littleneighbors.enums.LifeStage;
import com.alexandracoder.littleneighbors.family.entity.FamilyEntity;
import com.alexandracoder.littleneighbors.interest.entity.InterestEntity;
import com.alexandracoder.littleneighbors.shared.BaseEntity;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.Period;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "children")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChildEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "life_stage", nullable = false)
    private LifeStage lifeStage;

    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Gender gender;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Column(name = "is_prenatal", nullable = false)
    @Builder.Default
    private boolean isPrenatal = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "family_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JsonBackReference
    private FamilyEntity family;

    @Builder.Default
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "child_interests",
            joinColumns = @JoinColumn(name = "child_id"),
            inverseJoinColumns = @JoinColumn(name = "interest_id")
    )
    private Set<InterestEntity> interests = new HashSet<>();

    @Transient
    public int getAge() {
        if (this.isPrenatal || this.birthDate == null) {
            return 0;
        }
        return Period.between(this.birthDate, LocalDate.now()).getYears();
    }

    public void addInterest(InterestEntity interest) {
        this.interests.add(interest);
    }

    public void removeInterest(InterestEntity interest) {
        this.interests.remove(interest);
    }
}
