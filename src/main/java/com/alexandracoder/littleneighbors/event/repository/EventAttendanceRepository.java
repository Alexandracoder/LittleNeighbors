package com.alexandracoder.littleneighbors.event.repository;

import com.alexandracoder.littleneighbors.event.entity.EventAttendanceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface EventAttendanceRepository extends JpaRepository<EventAttendanceEntity, Long> {

    boolean existsByEventIdAndFamilyId(Long eventId, Long familyId);

    long countByEventId(Long eventId);

    @Transactional
    void deleteByEventIdAndFamilyId(Long eventId, Long familyId);

    @Query("SELECT a.event.id FROM EventAttendanceEntity a WHERE a.family.id = :familyId")
    List<Long> findAttendedEventIdsByFamilyId(@Param("familyId") Long familyId);

    // Conteo por lote para listados (evita N+1: una query para todos los
    // eventos visibles en vez de una por evento).
    @Query("SELECT a.event.id, COUNT(a) FROM EventAttendanceEntity a WHERE a.event.id IN :eventIds GROUP BY a.event.id")
    List<Object[]> countByEventIds(@Param("eventIds") List<Long> eventIds);
}
