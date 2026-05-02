package com.alexandracoder.littleneighbors.child.service;

import com.alexandracoder.littleneighbors.child.dto.ChildRequestDTO;
import com.alexandracoder.littleneighbors.child.dto.ChildResponseDTO;
import com.alexandracoder.littleneighbors.child.dto.ChildSummaryDTO;
import java.util.List;

public interface ChildService {

    List<ChildSummaryDTO> getAllSummaries();

    List<ChildResponseDTO> findAllByFamilyEmail(String email);


    ChildResponseDTO create(ChildRequestDTO dto, String username);

    ChildResponseDTO update(Long id, ChildRequestDTO dto, String username);

    ChildResponseDTO getById(Long id, String username);

    void deleteByIdAndFamilyEmail(Long id, String email);

}