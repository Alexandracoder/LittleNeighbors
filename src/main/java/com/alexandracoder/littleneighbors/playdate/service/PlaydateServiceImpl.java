package com.alexandracoder.littleneighbors.playdate.service;

import com.alexandracoder.littleneighbors.match.entity.MatchEntity;
import com.alexandracoder.littleneighbors.match.repository.MatchRepository;
import com.alexandracoder.littleneighbors.playdate.dto.PlaydateRequestDTO;
import com.alexandracoder.littleneighbors.playdate.entity.PlaydateEntity;
import com.alexandracoder.littleneighbors.playdate.repository.PlaydateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlaydateServiceImpl implements PlaydateService {

    private final PlaydateRepository playdateRepository;
    private final MatchRepository matchRepository;

    @Override
    @Transactional
    public PlaydateEntity createPlaydate(PlaydateRequestDTO dto) {
        MatchEntity match = matchRepository.findById(dto.matchId())
                .orElseThrow(() -> new RuntimeException("Match not found with ID: " + dto.matchId()));

        PlaydateEntity playdate = PlaydateEntity.builder()
                .title(dto.title())
                .startTime(dto.startTime())
                .description(dto.description())
                .match(match)
                .status("PENDING")
                .build();

        return playdateRepository.save(playdate);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlaydateEntity> findAllByFamily(Long familyId) {
        return playdateRepository.findByMatchChildRequestFamilyIdOrMatchChildTargetFamilyId(familyId, familyId);
    }
}