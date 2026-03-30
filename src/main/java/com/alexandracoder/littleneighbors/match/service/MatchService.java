package com.alexandracoder.littleneighbors.match.service;

import com.alexandracoder.littleneighbors.family.entity.FamilyEntity;
import com.alexandracoder.littleneighbors.match.entity.MatchEntity;

import java.util.List;

public interface MatchService {
    MatchEntity requestMatch(Long childAId, Long chidBId);
    List<FamilyEntity> findCompatibleFamilies(Long neighborhoodId, int minAge, int maxAge, List<Long> interestIds, Long currentChildId);
    void validateWeeklyConstraint(Long childId);
    boolean hasActiveMatchThisWeek(Long childId);
}
