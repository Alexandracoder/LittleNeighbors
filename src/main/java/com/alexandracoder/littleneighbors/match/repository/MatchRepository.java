package com.alexandracoder.littleneighbors.match.repository;

import com.alexandracoder.littleneighbors.enums.MatchStatus;
import com.alexandracoder.littleneighbors.match.dto.MatchResponseDetailDTO;
import com.alexandracoder.littleneighbors.match.entity.MatchEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MatchRepository extends JpaRepository<MatchEntity, Long>, JpaSpecificationExecutor<MatchEntity> {
        List<MatchEntity> findByChildAFamilyUserEmail(String email);
        List<MatchEntity> findByChildBFamilyUserEmail(String email);
    }
