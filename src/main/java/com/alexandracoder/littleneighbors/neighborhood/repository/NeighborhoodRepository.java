package com.alexandracoder.littleneighbors.neighborhood.repository;

import com.alexandracoder.littleneighbors.neighborhood.entity.NeighborhoodEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NeighborhoodRepository extends JpaRepository<NeighborhoodEntity, Long>, JpaSpecificationExecutor<NeighborhoodEntity> {

    Optional<NeighborhoodEntity> findByName(String name);

}
