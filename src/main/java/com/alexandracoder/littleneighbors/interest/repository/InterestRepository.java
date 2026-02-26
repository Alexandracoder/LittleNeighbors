package com.alexandracoder.littleneighbors.interest.repository;

import com.alexandracoder.littleneighbors.interest.entity.InterestEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InterestRepository extends JpaRepository<InterestEntity, Long> {
}