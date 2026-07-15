package com.alexandracoder.littleneighbors.playdate.service;

import com.alexandracoder.littleneighbors.playdate.dto.PlaydateRequestDTO;
import com.alexandracoder.littleneighbors.playdate.dto.PlaydateResponseDTO;

import java.util.List;

public interface PlaydateService {
    PlaydateResponseDTO createPlaydate(PlaydateRequestDTO dto, String currentUserEmail);

    List<PlaydateResponseDTO> findByMatchId(Long matchId, String currentUserEmail);

    List<PlaydateResponseDTO> findAllByUser(Long userId);

    PlaydateResponseDTO confirm(Long playdateId, String currentUserEmail);

    PlaydateResponseDTO reject(Long playdateId, String currentUserEmail);

    void deletePlaydate(Long playdateId, String currentUserEmail);
}