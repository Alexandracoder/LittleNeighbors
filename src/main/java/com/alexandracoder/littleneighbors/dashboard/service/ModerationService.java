package com.alexandracoder.littleneighbors.dashboard.service;

public interface ModerationService {
    void verifyUser(Long userId);
    void blockUser(Long userId);
}
