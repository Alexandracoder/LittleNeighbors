package com.alexandracoder.littleneighbors.city.entity;

import com.alexandracoder.littleneighbors.neighborhood.entity.NeighborhoodEntity;
import com.alexandracoder.littleneighbors.shared.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import java.util.HashSet;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "cities")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CityEntity extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 255)
    private String name;

    @Builder.Default
    @OneToMany(mappedBy = "city", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<NeighborhoodEntity> neighborhoods = new HashSet<>();
}