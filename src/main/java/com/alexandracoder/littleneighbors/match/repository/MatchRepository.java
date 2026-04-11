package com.alexandracoder.littleneighbors.match.repository;

import com.alexandracoder.littleneighbors.match.entity.MatchEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MatchRepository extends JpaRepository<MatchEntity, Long>, JpaSpecificationExecutor<MatchEntity> {

    List<MatchEntity> findByChildRequestFamilyUserEmail(String email);

    List<MatchEntity> findByChildTargetFamilyUserEmail(String email);
}