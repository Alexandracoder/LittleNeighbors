package com.alexandracoder.littleneighbors.sitevisit.repository;

import com.alexandracoder.littleneighbors.sitevisit.entity.SiteVisitEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SiteVisitRepository extends JpaRepository<SiteVisitEntity, Long> {

    @Query("SELECT COUNT(DISTINCT s.sessionId) FROM SiteVisitEntity s")
    long countDistinctSessionId();

    @Query("SELECT COUNT(DISTINCT s.sessionId) FROM SiteVisitEntity s WHERE s.createdAt >= :since")
    long countDistinctSessionIdSince(@Param("since") LocalDateTime since);

    // Visitantes únicos por día, para pintar la evolución en el panel de
    // admin. CAST(... AS date) agrupa por día ignorando la hora.
    @Query("SELECT CAST(s.createdAt AS date), COUNT(DISTINCT s.sessionId) " +
            "FROM SiteVisitEntity s WHERE s.createdAt >= :since " +
            "GROUP BY CAST(s.createdAt AS date) ORDER BY 1")
    List<Object[]> countDailyUniqueVisitorsSince(@Param("since") LocalDateTime since);
}
