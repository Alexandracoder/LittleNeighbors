package com.alexandracoder.littleneighbors.playdate.repository;

import com.alexandracoder.littleneighbors.playdate.entity.PlaydateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface PlaydateRepository extends JpaRepository<PlaydateEntity, Long>, JpaSpecificationExecutor<PlaydateEntity> {

    List<PlaydateEntity> findByMatchId(Long matchId);

    List<PlaydateEntity> findByMatchChildRequestFamilyIdOrMatchChildTargetFamilyId(Long familyId1, Long familyId2);
}
