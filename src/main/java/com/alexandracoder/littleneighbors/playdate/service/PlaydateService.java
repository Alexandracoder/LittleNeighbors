package com.alexandracoder.littleneighbors.playdate.service;

import com.alexandracoder.littleneighbors.playdate.dto.PlaydateRequestDTO;
import com.alexandracoder.littleneighbors.playdate.entity.PlaydateEntity;
import java.util.List;

public interface PlaydateService {
    PlaydateEntity createPlaydate(PlaydateRequestDTO dto);
    List<PlaydateEntity> findAllByFamily(Long familyId);
}