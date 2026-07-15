package com.alexandracoder.littleneighbors.event.repository;

import com.alexandracoder.littleneighbors.event.entity.EventDismissalEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventDismissalRepository extends JpaRepository<EventDismissalEntity, Long> {

    boolean existsByEventIdAndFamilyId(Long eventId, Long familyId);

    @Query("SELECT d.event.id FROM EventDismissalEntity d WHERE d.family.id = :familyId")
    List<Long> findDismissedEventIdsByFamilyId(@Param("familyId") Long familyId);
}
