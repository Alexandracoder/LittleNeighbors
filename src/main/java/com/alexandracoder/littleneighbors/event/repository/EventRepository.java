package com.alexandracoder.littleneighbors.event.repository;

import com.alexandracoder.littleneighbors.event.entity.EventEntity;
import com.alexandracoder.littleneighbors.neighborhood.dto.NeighborhoodResponseDTO;
import com.alexandracoder.littleneighbors.neighborhood.entity.NeighborhoodEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface EventRepository extends JpaRepository<EventEntity, Long>, JpaSpecificationExecutor<EventEntity> {

    List<EventEntity> findByNeighborhoodName(String name);
}