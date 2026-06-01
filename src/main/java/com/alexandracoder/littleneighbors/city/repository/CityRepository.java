package com.alexandracoder.littleneighbors.city.repository;

import com.alexandracoder.littleneighbors.city.entity.CityEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CityRepository extends JpaRepository<CityEntity, Long> {
    Optional<CityEntity> findByNameIgnoreCase(String valencia);
}