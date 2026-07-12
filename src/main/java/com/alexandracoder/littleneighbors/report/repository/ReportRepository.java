package com.alexandracoder.littleneighbors.report.repository;

import com.alexandracoder.littleneighbors.enums.ReportStatus;
import com.alexandracoder.littleneighbors.report.entity.ReportEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReportRepository extends JpaRepository<ReportEntity, Long> {

    List<ReportEntity> findByStatusOrderByCreatedAtAsc(ReportStatus status);
}
