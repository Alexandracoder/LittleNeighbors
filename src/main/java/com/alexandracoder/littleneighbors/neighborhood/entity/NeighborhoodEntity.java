package com.alexandracoder.littleneighbors.neighborhood.entity;

import com.alexandracoder.littleneighbors.city.entity.CityEntity;
import com.alexandracoder.littleneighbors.family.entity.FamilyEntity;
import com.alexandracoder.littleneighbors.shared.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "neighborhoods")
@Data
@NoArgsConstructor
@AllArgsConstructor
@lombok.experimental.SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class NeighborhoodEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 255)
    private String name;

    @Column(nullable = false)
    private String streetName;

    @Column(name = "postal_code", nullable = true)
    private String postalCode;

    // Centroide aproximado del barrio. Se usa como posición del pin en el
    // mapa de Explorar cuando la familia no tiene coordenadas propias (que
    // es siempre, hoy: no pedimos la dirección exacta por privacidad, dado
    // que hay menores implicados).
    private Double latitude;

    private Double longitude;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "city_id", nullable = false)
    private CityEntity city;

    @Builder.Default
    @OneToMany(mappedBy = "neighborhood", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<FamilyEntity> families = new HashSet<>();
}

