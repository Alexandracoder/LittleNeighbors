package com.alexandracoder.littleneighbors.neighborhood.service;

import com.alexandracoder.littleneighbors.neighborhood.dto.NeighborhoodRequestDTO;
import com.alexandracoder.littleneighbors.neighborhood.dto.NeighborhoodResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NeighborhoodService {

    Page<NeighborhoodResponseDTO> getAll(Pageable pageable);
    NeighborhoodResponseDTO getById(Long id);
    NeighborhoodResponseDTO create(NeighborhoodRequestDTO dto);
    NeighborhoodResponseDTO update(Long id, NeighborhoodRequestDTO dto);
    void delete(Long id);
}
