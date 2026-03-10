package com.alexandracoder.littleneighbors.family.service;

import com.alexandracoder.littleneighbors.family.dto.FamilyRequestDTO;
import com.alexandracoder.littleneighbors.family.dto.FamilyResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface FamilyService {
    FamilyResponseDTO getFamilyById(Long id, String name);
    FamilyResponseDTO getFamilyByEmail(String email);
    FamilyResponseDTO createFamily(FamilyRequestDTO dto, String username);
    FamilyResponseDTO updateFamily(Long id, FamilyRequestDTO dto, String username);
    void deleteFamily(Long id, String loggedUser);
    Page<FamilyResponseDTO> getAllFamilies(Pageable pageable);

    @Transactional(readOnly = true)
    List<FamilyResponseDTO> explorePlaymateFamilies(String userEmail, List<Long> interestId, Integer minAge, Integer maxAge);
}