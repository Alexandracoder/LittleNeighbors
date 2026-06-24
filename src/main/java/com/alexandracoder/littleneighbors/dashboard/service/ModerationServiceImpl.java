package com.alexandracoder.littleneighbors.dashboard.service;

import com.alexandracoder.littleneighbors.user.entity.UserEntity;
import com.alexandracoder.littleneighbors.enums.VerificationStatus;
import com.alexandracoder.littleneighbors.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ModerationServiceImpl implements ModerationService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public void verifyUser(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setVerificationStatus(VerificationStatus.VERIFIED);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void blockUser(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setVerificationStatus(VerificationStatus.BLOCKED);
        userRepository.save(user);
    }
    @Override
    @Transactional
    public void rejectUser(Long userId, String reason) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setVerificationStatus(VerificationStatus.REJECTED);
        user.setRejectionReason(reason);
        userRepository.save(user);
    }
}