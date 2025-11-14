package com.alexandracoder.littleneighbors.child.service;

import com.alexandracoder.littleneighbors.child.dto.ChildRequestDTO;
import com.alexandracoder.littleneighbors.child.dto.ChildResponseDTO;
import com.alexandracoder.littleneighbors.child.dto.ChildSummaryDTO;
import com.alexandracoder.littleneighbors.family.entity.FamilyEntity;


import java.util.List;

public interface ChildService {

    // ADMIN
    List<ChildSummaryDTO> getAllSummaries();

    // FAMILY (username = email del usuario autenticado)
    ChildResponseDTO create(ChildRequestDTO dto, String username);
    ChildResponseDTO update(Long id, ChildRequestDTO dto, String username);
    void delete(Long id, String username);
    ChildResponseDTO getById(Long id, String username);

    // AUXILIAR
    FamilyEntity getFamilyByUserEmail(String email);
}
