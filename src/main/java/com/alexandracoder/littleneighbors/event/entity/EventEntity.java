package com.alexandracoder.littleneighbors.event.entity;

import com.alexandracoder.littleneighbors.family.entity.FamilyEntity;
import com.alexandracoder.littleneighbors.neighborhood.entity.NeighborhoodEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "events")
@Data
@NoArgsConstructor
@AllArgsConstructor
@lombok.experimental.SuperBuilder
public class EventEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String title;

    private String description;

    @NotNull
    private LocalDateTime eventDate;

    @NotNull
    private Double latitude;

    @NotNull
    private Double longitude;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "neighborhood_id")
    private NeighborhoodEntity neighborhood;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_family_id")
    private FamilyEntity creatorFamily;
}