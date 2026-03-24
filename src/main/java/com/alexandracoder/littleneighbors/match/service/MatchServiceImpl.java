package com.alexandracoder.littleneighbors.match.service;

import com.alexandracoder.littleneighbors.child.entity.ChildEntity;
import com.alexandracoder.littleneighbors.child.repository.ChildRepository;
import com.alexandracoder.littleneighbors.enums.MatchStatus;
import com.alexandracoder.littleneighbors.family.entity.FamilyEntity;
import com.alexandracoder.littleneighbors.family.repository.FamilyRepository;
import com.alexandracoder.littleneighbors.match.entity.MatchEntity;
import com.alexandracoder.littleneighbors.match.repository.MatchRepository;
import com.alexandracoder.littleneighbors.specifications.MatchSpecifications;
import com.alexandracoder.littleneighbors.specifications.FamilySpecifications;
import com.alexandracoder.littleneighbors.shared.exceptions.ResourceNotFoundException;
import com.alexandracoder.littleneighbors.shared.exceptions.BusinessLogicException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MatchServiceImpl implements MatchService {

    private final MatchRepository matchRepository;
    private final ChildRepository childRepository;
    private final FamilyRepository familyRepository;

    @Override
    @Transactional
    public MatchEntity requestMatch(Long childAId, Long childBId) {
        if (childAId.equals(childBId)) {
            throw new BusinessLogicException("Cannot request a match with the same child.");
        }

        ChildEntity childA = childRepository.findById(childAId)
                .orElseThrow(() -> new ResourceNotFoundException("Initiator child not found ID: " + childAId));
        ChildEntity childB = childRepository.findById(childBId)
                .orElseThrow(() -> new ResourceNotFoundException("Target child not found ID: " + childBId));

        validateWeeklyConstraint(childAId);

        if (childA.getFamily().getNeighborhood() == null || childB.getFamily().getNeighborhood() == null) {
            throw new BusinessLogicException("Both families must have an assigned neighborhood.");
        }

        Long neighborhoodA = childA.getFamily().getNeighborhood().getId();
        Long neighborhoodB = childB.getFamily().getNeighborhood().getId();

        if (!neighborhoodA.equals(neighborhoodB)) {
            throw new BusinessLogicException("Connections are only allowed within the same neighborhood.");
        }

        MatchEntity newMatch = MatchEntity.builder()
                .childA(childA)
                .childB(childB)
                .status(MatchStatus.PENDING)
                .build();

        return matchRepository.save(newMatch);
    }

    @Override
    public List<FamilyEntity> findCompatibleFamilies(Long neighborhoodId, int minAge, int maxAge, List<Long> interestIds) {
        LocalDateTime oneWeekAgo = LocalDateTime.now().minusWeeks(1);

        Specification<FamilyEntity> spec = Specification
                .where(FamilySpecifications.hasNeighborhood(neighborhoodId))
                .and(FamilySpecifications.hasChildAgeBetween(minAge, maxAge));

        if (interestIds != null && !interestIds.isEmpty()) {
            spec = spec.and(FamilySpecifications.hasChildWithInterest(interestIds));
        }

        spec = spec.and(FamilySpecifications.hasNoRecentMatch(oneWeekAgo));

        return familyRepository.findAll(spec);
    }

    @Override
    public void validateWeeklyConstraint(Long childId) {
        if (hasActiveMatchThisWeek(childId)) {
            throw new BusinessLogicException("Only one match request per week is allowed.");
        }
    }

    @Override
    public boolean hasActiveMatchThisWeek(Long childId) {
        LocalDateTime oneWeekAgo = LocalDateTime.now().minusWeeks(1);
        long count = matchRepository.count(MatchSpecifications.hasMatchForChildInLastWeek(childId, oneWeekAgo));
        return count > 0;
    }
}
