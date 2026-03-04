package com.alexandracoder.littleneighbors.child.service;

import com.alexandracoder.littleneighbors.child.dto.ChildRequestDTO;
import com.alexandracoder.littleneighbors.child.dto.ChildResponseDTO;
import com.alexandracoder.littleneighbors.child.dto.ChildSummaryDTO;
import com.alexandracoder.littleneighbors.family.entity.FamilyEntity;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;

public interface ChildService {

    // ADMIN
    List<ChildSummaryDTO> getAllSummaries();

    // --- ESTE MÉTODO ES EL QUE EVITA QUE SE MEZCLEN LOS NIÑOS ---
    @Transactional(readOnly = true)
    List<ChildResponseDTO> findAllByFamilyEmail(String email);

    // FAMILY (username = email del usuario autenticado)
    ChildResponseDTO create(ChildRequestDTO dto, String username);
    ChildResponseDTO update(Long id, ChildRequestDTO dto, String username);
    void delete(Long id, String username);
    ChildResponseDTO getById(Long id, String username);

    // AUXILIAR
    FamilyEntity getFamilyByUserEmail(String email);
}
