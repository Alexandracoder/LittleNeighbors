package com.alexandracoder.littleneighbors.neighborhood.repository;

import com.alexandracoder.littleneighbors.neighborhood.entity.NeighborhoodEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NeighborhoodRepository extends JpaRepository<NeighborhoodEntity, Long> {
}
