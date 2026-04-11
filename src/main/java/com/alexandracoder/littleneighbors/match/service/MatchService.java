package com.alexandracoder.littleneighbors.match.service;

import com.alexandracoder.littleneighbors.family.entity.FamilyEntity;
import com.alexandracoder.littleneighbors.match.dto.MatchResponseDetailDTO;
import com.alexandracoder.littleneighbors.match.entity.MatchEntity;
import com.alexandracoder.littleneighbors.enums.MatchStatus;

import java.util.List;

public interface MatchService {
    MatchEntity requestMatch(Long childRequestId, Long childTargetId);

    List<FamilyEntity> findCompatibleFamilies(Long neighborhoodId, int minAge, int maxAge, List<Long> interestIds, Long currentChildId);

    void validateWeeklyConstraint(Long childId);

    boolean hasActiveMatchThisWeek(Long childId);

    List<MatchResponseDetailDTO> getMatchesForUser(String email);

    void respondToMatch(Long matchId, MatchStatus status, String currentUserEmail);
}