package com.alexandracoder.littleneighbors.report.dto;

import jakarta.validation.constraints.NotNull;

public record ResolveReportRequestDTO(
        @NotNull ReportResolutionAction action
) {
    public enum ReportResolutionAction {
        DISMISS,
        BLOCK_ACCOUNT
    }
}
