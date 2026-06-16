package com.alexandracoder.littleneighbors.interest.service;

import com.alexandracoder.littleneighbors.interest.dto.InterestResponseDTO;

import java.util.List;

public interface InterestService {
    List<InterestResponseDTO> findAll();
    InterestResponseDTO findById(Long id);
}
