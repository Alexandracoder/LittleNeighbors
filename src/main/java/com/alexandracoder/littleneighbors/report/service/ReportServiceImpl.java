package com.alexandracoder.littleneighbors.report.service;

import com.alexandracoder.littleneighbors.dashboard.service.ModerationService;
import com.alexandracoder.littleneighbors.family.entity.FamilyEntity;
import com.alexandracoder.littleneighbors.family.repository.FamilyRepository;
import com.alexandracoder.littleneighbors.report.dto.AdminReportDTO;
import com.alexandracoder.littleneighbors.report.dto.CreateReportRequestDTO;
import com.alexandracoder.littleneighbors.report.dto.ResolveReportRequestDTO;
import com.alexandracoder.littleneighbors.report.entity.ReportEntity;
import com.alexandracoder.littleneighbors.report.repository.ReportRepository;
import com.alexandracoder.littleneighbors.enums.ReportStatus;
import com.alexandracoder.littleneighbors.shared.exceptions.ResourceNotFoundException;
import com.alexandracoder.littleneighbors.user.entity.UserEntity;
import com.alexandracoder.littleneighbors.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository;
    private final UserRepository userRepository;
    private final FamilyRepository familyRepository;
    private final ModerationService moderationService;

    @Override
    @Transactional
    public void createReport(String reporterEmail, CreateReportRequestDTO dto) {
        UserEntity reporter = userRepository.findByEmail(reporterEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        FamilyEntity reportedFamily = null;
        Long familyIdToResolve = dto.reportedFamilyId() != null ? dto.reportedFamilyId() : dto.relatedId();

        if (familyIdToResolve != null) {
            reportedFamily = familyRepository.findById(familyIdToResolve).orElse(null);
        }

        UserEntity reportedUser = reportedFamily != null ? reportedFamily.getUser() : null;

        if (reportedUser != null && reportedUser.getId().equals(reporter.getId())) {
            throw new IllegalStateException("You cannot report your own account");
        }

        ReportEntity report = ReportEntity.builder()
                .reporterUser(reporter)
                .reportedUser(reportedUser)
                .reportedFamily(reportedFamily)
                .reportType(dto.reportType())
                .relatedId(dto.relatedId())
                .reason(dto.reason())
                .description(dto.description())
                .contentSnapshot(dto.contentSnapshot())
                .status(ReportStatus.PENDING)
                .build();

        reportRepository.save(report);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AdminReportDTO> listPendingReports() {
        return reportRepository.findByStatusOrderByCreatedAtAsc(ReportStatus.PENDING).stream()
                .map(this::toAdminDTO)
                .toList();
    }

    @Override
    @Transactional
    public void resolveReport(Long reportId, ResolveReportRequestDTO dto, String adminEmail) {
        ReportEntity report = reportRepository.findById(reportId)
                .orElseThrow(() -> new ResourceNotFoundException("Report not found"));

        UserEntity admin = userRepository.findByEmail(adminEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Admin user not found"));

        if (dto.action() == ResolveReportRequestDTO.ReportResolutionAction.BLOCK_ACCOUNT) {
            if (report.getReportedUser() == null) {
                throw new IllegalStateException(
                        "This report has no resolvable account to block (no family/user linked)");
            }
            moderationService.blockUser(report.getReportedUser().getId());
        }

        boolean accountBlocked = dto.action() == ResolveReportRequestDTO.ReportResolutionAction.BLOCK_ACCOUNT;
        report.setStatus(accountBlocked ? ReportStatus.ACTION_TAKEN : ReportStatus.DISMISSED);
        report.setResolvedAt(LocalDateTime.now());
        report.setResolvedByAdmin(admin);

        reportRepository.save(report);
    }

    private AdminReportDTO toAdminDTO(ReportEntity r) {
        String reporterFamilyName = null;
        try {
            reporterFamilyName = familyRepository.findByUserEmail(r.getReporterUser().getEmail())
                    .map(FamilyEntity::getFamilyName)
                    .orElse(null);
        } catch (Exception ignored) {
            // el reporter puede no tener familia todavía; no es crítico para mostrar el reporte
        }

        return new AdminReportDTO(
                r.getId(),
                reporterFamilyName,
                r.getReporterUser().getEmail(),
                r.getReportedFamily() != null ? r.getReportedFamily().getFamilyName() : null,
                r.getReportedFamily() != null ? r.getReportedFamily().getId() : null,
                r.getReportedUser() != null ? r.getReportedUser().getId() : null,
                r.getReportType(),
                r.getRelatedId(),
                r.getReason(),
                r.getDescription(),
                r.getContentSnapshot(),
                r.getStatus(),
                r.getCreatedAt()
        );
    }
}
