package com.alexandracoder.littleneighbors.report.service;

import com.alexandracoder.littleneighbors.report.dto.AdminReportDTO;
import com.alexandracoder.littleneighbors.report.dto.CreateReportRequestDTO;
import com.alexandracoder.littleneighbors.report.dto.ResolveReportRequestDTO;

import java.util.List;

public interface ReportService {

    void createReport(String reporterEmail, CreateReportRequestDTO dto);

    List<AdminReportDTO> listPendingReports();

    void resolveReport(Long reportId, ResolveReportRequestDTO dto, String adminEmail);
}
