package com.alexandracoder.littleneighbors.match.service;

import com.alexandracoder.littleneighbors.child.entity.ChildEntity;
import com.alexandracoder.littleneighbors.child.repository.ChildRepository;
import com.alexandracoder.littleneighbors.enums.MatchStatus;
import com.alexandracoder.littleneighbors.family.entity.FamilyEntity;
import com.alexandracoder.littleneighbors.family.repository.FamilyRepository;
import com.alexandracoder.littleneighbors.match.dto.MatchResponseDetailDTO;
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
import java.util.stream.Stream;

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
            throw new BusinessLogicException("You cannot request a match with the same child.");
        }

        ChildEntity childA = childRepository.findById(childAId)
                .orElseThrow(() -> new ResourceNotFoundException("Initiator child not found: " + childAId));
        ChildEntity childB = childRepository.findById(childBId)
                .orElseThrow(() -> new ResourceNotFoundException("Target child not found: " + childBId));

        validateWeeklyConstraint(childAId);

        if (childA.getFamily().getNeighborhood() == null || childB.getFamily().getNeighborhood() == null) {
            throw new BusinessLogicException("Both families must have a neighborhood assigned to connect.");
        }

        if (!childA.getFamily().getNeighborhood().getId().equals(childB.getFamily().getNeighborhood().getId())) {
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
    @Transactional(readOnly = true)
    public List<FamilyEntity> findCompatibleFamilies(Long neighborhoodId, int minAge, int maxAge, List<Long> interestIds, Long currentChildId) {
        if (neighborhoodId == null) return List.of();

        LocalDateTime oneWeekAgo = LocalDateTime.now().minusWeeks(1);

        Long myFamilyId = null;
        if (currentChildId != null) {
            myFamilyId = childRepository.findById(currentChildId)
                    .map(child -> child.getFamily().getId())
                    .orElse(null);
        }

        Specification<FamilyEntity> spec = Specification
                .where(FamilySpecifications.hasNeighborhood(neighborhoodId))
                .and(FamilySpecifications.hasChildWithCriteria(minAge, maxAge, interestIds))
                .and(FamilySpecifications.hasNoRecentMatch(oneWeekAgo))
                .and(FamilySpecifications.isNotChild(currentChildId))
                .and(FamilySpecifications.isNotMyFamily(myFamilyId));

        return familyRepository.findAll(spec);
    }

    @Override
    public void validateWeeklyConstraint(Long childId) {
        if (hasActiveMatchThisWeek(childId)) {
            throw new BusinessLogicException("Only one match request is allowed per week.");
        }
    }

    @Override
    public boolean hasActiveMatchThisWeek(Long childId) {
        if (childId == null) return false;
        LocalDateTime oneWeekAgo = LocalDateTime.now().minusWeeks(1);
        return matchRepository.exists(MatchSpecifications.hasMatchForChildInLastWeek(childId, oneWeekAgo));
    }

    @Override
    @Transactional(readOnly = true)
    public List<MatchResponseDetailDTO> getMatchesForUser(String email) {
        List<MatchEntity> matchesAsA = matchRepository.findByChildAFamilyUserEmail(email);
        List<MatchEntity> matchesAsB = matchRepository.findByChildBFamilyUserEmail(email);

        return Stream.concat(matchesAsA.stream(), matchesAsB.stream())
                .map(match -> convertToDetailDTO(match, email))
                .toList();
    }

    @Override
    @Transactional
    public void respondToMatch(Long matchId, MatchStatus status, String currentUserEmail) {
        MatchEntity match = matchRepository.findById(matchId)
                .orElseThrow(() -> new ResourceNotFoundException("Match not found: " + matchId));

        boolean isTarget = match.getChildB().getFamily().getUser().getEmail().equals(currentUserEmail);

        if (!isTarget) {
            throw new BusinessLogicException("You do not have permission to respond to this request; you are not the recipient.");
        }

        if (match.getStatus() != MatchStatus.PENDING) {
            throw new BusinessLogicException("This match has already been processed.");
        }

        match.setStatus(status);
        matchRepository.save(match);
    }

    private MatchResponseDetailDTO convertToDetailDTO(MatchEntity match, String currentUserEmail) {
        ChildEntity childA = match.getChildA();
        ChildEntity childB = match.getChildB();

        boolean isChildA = childA.getFamily().getUser().getEmail().equals(currentUserEmail);

        ChildEntity myChild = isChildA ? childA : childB;
        ChildEntity theirChild = isChildA ? childB : childA;

        return MatchResponseDetailDTO.builder()
                .matchId(match.getId())
                .status(match.getStatus())
                .myChildId(myChild.getId())
                .myChildGender(myChild.getGender().toString())
                .theirChildId(theirChild.getId())
                .theirChildGender(theirChild.getGender().toString())
                .theirFamilyName(theirChild.getFamily().getFamilyName())
                .theirNeighborhoodName(theirChild.getFamily().getNeighborhood().getName())
                .build();
    }
}
