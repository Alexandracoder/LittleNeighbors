package com.alexandracoder.littleneighbors.child.service;

import com.alexandracoder.littleneighbors.child.dto.ChildRequestDTO;
import com.alexandracoder.littleneighbors.child.dto.ChildResponseDTO;
import com.alexandracoder.littleneighbors.child.dto.ChildSummaryDTO;

import java.util.List;

public interface ChildService {

    ChildResponseDTO create(ChildRequestDTO dto);

    ChildResponseDTO update(Long id, ChildRequestDTO dto);

    void delete(Long id);

    ChildResponseDTO getById(Long id);

    List<ChildSummaryDTO> getAllSummaries();
}
