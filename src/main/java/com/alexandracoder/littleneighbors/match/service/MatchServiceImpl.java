package com.alexandracoder.littleneighbors.match.service;

import com.alexandracoder.littleneighbors.child.entity.ChildEntity;
import com.alexandracoder.littleneighbors.child.repository.ChildRepository;
import com.alexandracoder.littleneighbors.enums.MatchStatus;
import com.alexandracoder.littleneighbors.enums.VerificationStatus;
import com.alexandracoder.littleneighbors.family.entity.FamilyEntity;
import com.alexandracoder.littleneighbors.family.repository.FamilyRepository;
import com.alexandracoder.littleneighbors.match.dto.MatchResponseDetailDTO;
import com.alexandracoder.littleneighbors.match.entity.MatchEntity;
import com.alexandracoder.littleneighbors.match.repository.MatchRepository;
import com.alexandracoder.littleneighbors.notification.service.NotificationService;
import com.alexandracoder.littleneighbors.specifications.MatchSpecifications;
import com.alexandracoder.littleneighbors.specifications.FamilySpecifications;
import com.alexandracoder.littleneighbors.shared.exceptions.ResourceNotFoundException;
import com.alexandracoder.littleneighbors.shared.exceptions.BusinessLogicException;
import com.alexandracoder.littleneighbors.user.entity.UserEntity;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Stream;

@Service
public class MatchServiceImpl implements MatchService {

    private final MatchRepository matchRepository;
    private final ChildRepository childRepository;
    private final FamilyRepository familyRepository;
    private final NotificationService notificationService;

    private final boolean demoMode;

    public MatchServiceImpl(
            MatchRepository matchRepository,
            ChildRepository childRepository,
            FamilyRepository familyRepository,
            NotificationService notificationService,
            @Value("${app.demo-mode:false}") boolean demoMode) {
        this.matchRepository = matchRepository;
        this.childRepository = childRepository;
        this.familyRepository = familyRepository;
        this.notificationService = notificationService;
        this.demoMode = demoMode;
    }

    @Override
    @Transactional
    public MatchEntity requestMatch(Long childRequestId, Long childTargetId) {
        if (childRequestId.equals(childTargetId)) {
            throw new BusinessLogicException("You cannot request a match with the same child.");
        }

        ChildEntity childRequest = childRepository.findById(childRequestId)
                .orElseThrow(() -> new ResourceNotFoundException("Initiator child not found: " + childRequestId));
        ChildEntity childTarget = childRepository.findById(childTargetId)
                .orElseThrow(() -> new ResourceNotFoundException("Target child not found: " + childTargetId));


        if (!demoMode) {
            UserEntity currentUser = childRequest.getFamily().getUser();
            if (currentUser.getVerificationStatus() != VerificationStatus.VERIFIED) {
                throw new BusinessLogicException("Your account must be VERIFIED to request a match.");
            }
        }

        if (childRequest.getFamily().getNeighborhood() == null || childTarget.getFamily().getNeighborhood() == null) {
            throw new BusinessLogicException("Both families must have a neighborhood assigned to connect.");
        }

        if (!childRequest.getFamily().getNeighborhood().getId().equals(childTarget.getFamily().getNeighborhood().getId())) {
            throw new BusinessLogicException("Connections are only allowed within the same neighborhood.");
        }

        LocalDateTime oneWeekAgo = LocalDateTime.now().minusWeeks(1);

        return matchRepository.findAll(MatchSpecifications.hasMatchForChildInLastWeek(childRequestId, oneWeekAgo))
                .stream()
                .filter(m -> m.getChildRequest().getId().equals(childTargetId) || m.getChildTarget().getId().equals(childTargetId))
                .findFirst()
                .orElseGet(() -> {
                    MatchEntity newMatch = MatchEntity.builder()
                            .childRequest(childRequest)
                            .childTarget(childTarget)
                            .status(MatchStatus.PENDING)
                            .build();
                    return matchRepository.save(newMatch);
                });
    }

    @Override
    @Transactional(readOnly = true)
    public List<FamilyEntity> findCompatibleFamilies(Long neighborhoodId, int minAge, int maxAge, List<Long> interestIds, Long currentChildId) {
        if (neighborhoodId == null) return List.of();

        Long myFamilyId = null;
        if (currentChildId != null) {
            myFamilyId = childRepository.findById(currentChildId)
                    .map(child -> child.getFamily().getId())
                    .orElse(null);
        }

        Specification<FamilyEntity> spec = Specification
                .where(FamilySpecifications.hasNeighborhood(neighborhoodId))
                .and(FamilySpecifications.hasChildWithCriteria(minAge, maxAge, interestIds))
                .and(FamilySpecifications.isNotChild(currentChildId))
                .and(FamilySpecifications.isNotMyFamily(myFamilyId));

        return familyRepository.findAll(spec);
    }

    @Override
    @Transactional
    public void respondToMatch(Long matchId, MatchStatus status, String currentUserEmail) {
        MatchEntity match = matchRepository.findById(matchId)
                .orElseThrow(() -> new ResourceNotFoundException("Match not found: " + matchId));

        UserEntity currentUser = match.getChildTarget().getFamily().getUser();
        if (!currentUser.getEmail().equals(currentUserEmail)) {
            throw new BusinessLogicException("You do not have permission to respond to this request.");
        }


        if (!demoMode) {
            if (currentUser.getVerificationStatus() != VerificationStatus.VERIFIED) {
                throw new BusinessLogicException("You must be VERIFIED to respond to matches.");
            }
        }

        if (match.getStatus() != MatchStatus.PENDING) {
            throw new BusinessLogicException("This match has already been processed.");
        }

        if (status == MatchStatus.ACCEPTED) {
            validateWeeklyConstraint(match.getChildTarget().getId());
            validateWeeklyConstraint(match.getChildRequest().getId());
        }

        match.setStatus(status);
        matchRepository.save(match);
    }

    @Override
    public void validateWeeklyConstraint(Long childId) {
        if (countAcceptedMatchesThisWeek(childId) >= 2) {
            throw new BusinessLogicException("Limit reached: Only 2 official connections are allowed per week.");
        }
    }

    @Override
    public boolean hasActiveMatchThisWeek(Long childId) {
        return countAcceptedMatchesThisWeek(childId) > 0;
    }

    private long countAcceptedMatchesThisWeek(Long childId) {
        if (childId == null) return 0;
        LocalDateTime oneWeekAgo = LocalDateTime.now().minusWeeks(1);

        Specification<MatchEntity> spec = Specification.where(MatchSpecifications.hasMatchForChildInLastWeek(childId, oneWeekAgo))
                .and((root, query, cb) -> cb.equal(root.get("status"), MatchStatus.ACCEPTED));

        return matchRepository.count(spec);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MatchResponseDetailDTO> getMatchesForUser(String email) {
        List<MatchEntity> matchesAsRequest = matchRepository.findByChildRequestFamilyUserEmail(email);
        List<MatchEntity> matchesAsTarget = matchRepository.findByChildTargetFamilyUserEmail(email);

        return Stream.concat(matchesAsRequest.stream(), matchesAsTarget.stream())
                .distinct()
                .map(match -> convertToDetailDTO(match, email))
                .toList();
    }

    private MatchResponseDetailDTO convertToDetailDTO(MatchEntity match, String currentUserEmail) {
        ChildEntity childRequest = match.getChildRequest();
        ChildEntity childTarget = match.getChildTarget();

        boolean isRequester = childRequest.getFamily().getUser().getEmail().equals(currentUserEmail);

        ChildEntity myChild = isRequester ? childRequest : childTarget;
        ChildEntity theirChild = isRequester ? childTarget : childRequest;

        return MatchResponseDetailDTO.builder()
                .matchId(match.getId())
                .status(match.getStatus())
                .myChildId(myChild.getId())
                .myChildGender(myChild.getGender().toString())
                .theirChildId(theirChild.getId())
                .theirChildGender(theirChild.getGender().toString())
                .theirFamilyName(theirChild.getFamily().getFamilyName())
                .theirNeighborhoodName(theirChild.getFamily().getNeighborhood() != null ?
                        theirChild.getFamily().getNeighborhood().getName() : "N/A")
                .build();
    }

    @Transactional
    @Override
    public void confirmMatch(Long matchId, String userEmail) {
        MatchEntity match = matchRepository.findById(matchId)
                .orElseThrow(() -> new ResourceNotFoundException("Match not found"));

        UserEntity requester = match.getChildRequest().getFamily().getUser();
        UserEntity target = match.getChildTarget().getFamily().getUser();

        if (userEmail.equals(requester.getEmail())) {
            if (!demoMode && requester.getVerificationStatus() != VerificationStatus.VERIFIED) {
                throw new BusinessLogicException("You must be VERIFIED to confirm a match.");
            }
            match.setUserAccepted(true);
        } else if (userEmail.equals(target.getEmail())) {

            if (!demoMode && target.getVerificationStatus() != VerificationStatus.VERIFIED) {
                throw new BusinessLogicException("You must be VERIFIED to confirm a match.");
            }
            match.setNeighborAccepted(true);
        } else {
            throw new BusinessLogicException("Not authorized to confirm this match.");
        }

        if (match.isUserAccepted() && match.isNeighborAccepted()) {
            validateWeeklyConstraint(match.getChildRequest().getId());
            validateWeeklyConstraint(match.getChildTarget().getId());

            match.setStatus(MatchStatus.ACCEPTED);
            notificationService.sendMatchSuccessNotification(match);
        }

        matchRepository.save(match);
    }
}