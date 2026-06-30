package com.alexandracoder.littleneighbors.neighborhood.repository;

import com.alexandracoder.littleneighbors.neighborhood.entity.NeighborhoodEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface NeighborhoodRepository extends JpaRepository<NeighborhoodEntity, Long> {
    Optional<NeighborhoodEntity> findById(Long id);

    Optional<NeighborhoodEntity> findByNameIgnoreCase(String benimaclet);

    Optional<NeighborhoodEntity> findByName(String name);

}
