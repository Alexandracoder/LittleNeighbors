package com.alexandracoder.littleneighbors.dashboard.service;

import com.alexandracoder.littleneighbors.enums.PhotoModerationStatus;
import com.alexandracoder.littleneighbors.family.dto.FamilyMapper;
import com.alexandracoder.littleneighbors.family.dto.FamilyResponseDTO;
import com.alexandracoder.littleneighbors.family.entity.FamilyEntity;
import com.alexandracoder.littleneighbors.family.repository.FamilyRepository;
import com.alexandracoder.littleneighbors.shared.exceptions.ResourceNotFoundException;
import com.alexandracoder.littleneighbors.user.entity.UserEntity;
import com.alexandracoder.littleneighbors.enums.VerificationStatus;
import com.alexandracoder.littleneighbors.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ModerationServiceImpl implements ModerationService {

    private final UserRepository userRepository;
    private final FamilyRepository familyRepository;
    private final FamilyMapper familyMapper;

    @Override
    @Transactional
    public void verifyUser(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setVerificationStatus(VerificationStatus.VERIFIED);
        // Minimización de datos: en cuanto está verificada, no hace falta
        // seguir guardando el documento de identidad ni el selfie — solo
        // el resultado (verificationStatus).
        user.setIdDocumentUrl(null);
        user.setSelfieUrl(null);
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void blockUser(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setVerificationStatus(VerificationStatus.BLOCKED);
        user.setIdDocumentUrl(null);
        user.setSelfieUrl(null);
        userRepository.save(user);
    }
    @Override
    @Transactional
    public void rejectUser(Long userId, String reason) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setVerificationStatus(VerificationStatus.REJECTED);
        user.setRejectionReason(reason);
        user.setIdDocumentUrl(null);
        user.setSelfieUrl(null);
        userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<FamilyResponseDTO> getPendingPhotos() {
        return familyRepository.findByPhotoModerationStatus(PhotoModerationStatus.PENDING).stream()
                // toResponse (no toPublicResponse): el admin necesita ver la
                // foto pendiente para poder revisarla.
                .map(familyMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public void approvePhoto(Long familyId) {
        FamilyEntity family = familyRepository.findById(familyId)
                .orElseThrow(() -> new ResourceNotFoundException("Family not found with id: " + familyId));
        family.setPhotoModerationStatus(PhotoModerationStatus.APPROVED);
        family.setPhotoRejectionReason(null);
        familyRepository.save(family);
    }

    @Override
    @Transactional
    public void rejectPhoto(Long familyId, String reason) {
        FamilyEntity family = familyRepository.findById(familyId)
                .orElseThrow(() -> new ResourceNotFoundException("Family not found with id: " + familyId));
        // No borramos la URL: así la familia sigue viendo en su propio
        // perfil qué foto subió y por qué la rechazamos, y puede subir
        // una nueva sin perder el resto de sus datos.
        family.setPhotoModerationStatus(PhotoModerationStatus.REJECTED);
        family.setPhotoRejectionReason(reason);
        familyRepository.save(family);
    }
}