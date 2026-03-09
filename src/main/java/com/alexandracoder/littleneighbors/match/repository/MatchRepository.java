package com.alexandracoder.littleneighbors.match.repository;

import com.alexandracoder.littleneighbors.match.entity.MatchEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface MatchRepository extends JpaRepository<MatchEntity, Long>, JpaSpecificationExecutor<MatchEntity> {

    // Tu método derivado está bien, pero asegúrate de que los nombres de los parámetros
    // coincidan con los nombres de las columnas en MatchEntity.
    // Como tu entidad usa 'childA' y 'childB', Spring espera 'ChildAId' y 'ChildBId'.
    boolean existsByChildAIdOrChildBIdAndCreatedAtAfter(Long childAId, Long childBId, LocalDateTime createdAtAfter);
}