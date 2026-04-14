package com.alexandracoder.littleneighbors.playdate.service;

import com.alexandracoder.littleneighbors.enums.PlaydateStatus;
import com.alexandracoder.littleneighbors.match.entity.MatchEntity;
import com.alexandracoder.littleneighbors.match.repository.MatchRepository;
import com.alexandracoder.littleneighbors.playdate.dto.PlaydateRequestDTO;
import com.alexandracoder.littleneighbors.playdate.entity.PlaydateEntity;
import com.alexandracoder.littleneighbors.playdate.repository.PlaydateRepository;
import com.alexandracoder.littleneighbors.shared.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
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
    public PlaydateEntity createPlaydate(PlaydateRequestDTO dto, String currentUserEmail) {
        MatchEntity match = matchRepository.findById(dto.matchId())
                .orElseThrow(() -> new ResourceNotFoundException("Match not found with ID: " + dto.matchId()));


        String initiatorEmail = match.getChildRequest().getFamily().getUser().getEmail();
        String targetEmail = match.getChildTarget().getFamily().getUser().getEmail();

        if (!currentUserEmail.equals(initiatorEmail) && !currentUserEmail.equals(targetEmail)) {
            throw new AccessDeniedException("You are not part of this match");
        }

        PlaydateEntity playdate = PlaydateEntity.builder()
                .title(dto.title())
                .startTime(dto.startTime())
                .description(dto.description())
                .match(match)
                .status(PlaydateStatus.PENDING)
                .build();

        return playdateRepository.save(playdate);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlaydateEntity> findByMatchId(Long matchId) {

        return playdateRepository.findByMatchId(matchId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PlaydateEntity> findAllByFamily(Long familyId) {
        return playdateRepository.findByMatchChildRequestFamilyIdOrMatchChildTargetFamilyId(familyId, familyId);
    }
}