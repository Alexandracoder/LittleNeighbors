package com.alexandracoder.littleneighbors.family.service;

import com.alexandracoder.littleneighbors.family.dto.FamilyRequestDTO;
import com.alexandracoder.littleneighbors.family.dto.FamilyResponseDTO;
import com.alexandracoder.littleneighbors.family.dto.OnboardingResponseDTO; // 🆕 Importamos el nuevo DTO
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface FamilyService {
    FamilyResponseDTO getFamilyById(Long id, String name);
    FamilyResponseDTO getFamilyByEmail(String email);

    OnboardingResponseDTO createFamily(FamilyRequestDTO dto, String username);

    FamilyResponseDTO updateFamily(Long id, FamilyRequestDTO dto, String username);
    void deleteFamily(Long id, String loggedUser);
    Page<FamilyResponseDTO> getAllFamilies(Pageable pageable);
    List<FamilyResponseDTO> explorePlaymateFamilies(
            String userEmail,
            Long currentChildId,
            List<Long> interestIds,
            Integer minAge,
            Integer maxAge
    );
}