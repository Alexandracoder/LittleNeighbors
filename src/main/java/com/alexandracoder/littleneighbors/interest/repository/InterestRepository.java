package com.alexandracoder.littleneighbors.interest.repository;

import com.alexandracoder.littleneighbors.interest.entity.InterestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InterestRepository extends JpaRepository<InterestEntity, Long> {
    // No necesitas añadir nada más por ahora
}