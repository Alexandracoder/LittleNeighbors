package com.alexandracoder.littleneighbors.dashboard.service;

import com.alexandracoder.littleneighbors.family.dto.FamilyResponseDTO;

import java.util.List;

public interface ModerationService {
    void verifyUser(Long userId);
    void blockUser(Long userId);
    void rejectUser(Long userId, String reason);

    List<FamilyResponseDTO> getPendingPhotos();
    void approvePhoto(Long familyId);
    void rejectPhoto(Long familyId, String reason);
}
