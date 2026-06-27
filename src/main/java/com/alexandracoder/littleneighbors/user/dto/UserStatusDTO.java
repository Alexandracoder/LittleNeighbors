package com.alexandracoder.littleneighbors.user.dto;

import com.alexandracoder.littleneighbors.enums.VerificationStatus;

import java.util.List;

public record UserStatusDTO(
        boolean hasFamily,
        boolean hasChildren,
        boolean isRegistrationComplete,
        VerificationStatus verificationStatus,
        List<String> roles
) {
    public UserStatusDTO(boolean hasFamily, boolean hasChildren) {
        this(hasFamily, hasChildren, hasFamily && hasChildren, VerificationStatus.UNVERIFIED, List.of());
    }
}