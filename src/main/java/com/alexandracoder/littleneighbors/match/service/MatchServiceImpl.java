package com.alexandracoder.littleneighbors.match.service;

import com.alexandracoder.littleneighbors.child.entity.ChildEntity;
import com.alexandracoder.littleneighbors.child.repository.ChildRepository;
import com.alexandracoder.littleneighbors.enums.MatchStatus;
import com.alexandracoder.littleneighbors.enums.VerificationStatus;
import com.alexandracoder.littleneighbors.family.entity.FamilyEntity;
import com.alexandracoder.littleneighbors.enums.NotificationType;
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
import com.alexandracoder.littleneighbors.block.service.BlockService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
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
    private final BlockService blockService;
    private final boolean demoMode;
    private final int weeklyMatchLimit;

    public MatchServiceImpl(
            MatchRepository matchRepository,
            ChildRepository childRepository,
            FamilyRepository familyRepository,
            NotificationService notificationService,
            BlockService blockService,
            @Value("${app.demo-mode:false}") boolean demoMode,
            @Value("${app.matching.weekly-limit:4}") int weeklyMatchLimit) {
        this.matchRepository = matchRepository;
        this.childRepository = childRepository;
        this.familyRepository = familyRepository;
        this.notificationService = notificationService;
        this.blockService = blockService;
        this.demoMode = demoMode;
        this.weeklyMatchLimit = weeklyMatchLimit;
    }

    private boolean canBypassVerification(UserEntity user) {
        boolean isAdmin = user.getRoles().stream().anyMatch(role -> role.name().equals("ROLE_ADMIN"));
        return demoMode || isAdmin;
    }

    private void validateUserInMatch(MatchEntity match, String email) {
        String requesterEmail = match.getChildRequest().getFamily().getUser().getEmail();
        String targetEmail = match.getChildTarget().getFamily().getUser().getEmail();

        if (!requesterEmail.equals(email) && !targetEmail.equals(email)) {
            throw new AccessDeniedException("You do not have permission to access this match.");
        }
    }

    @Transactional
    @Override
    public MatchEntity requestMatch(Long childRequestId, Long childTargetId, String userEmail) {
        if (childRequestId.equals(childTargetId)) {
            throw new BusinessLogicException("You cannot request a match with the same child.");
        }

        ChildEntity childRequest = childRepository.findById(childRequestId)
                .orElseThrow(() -> new ResourceNotFoundException("Initiator child not found: " + childRequestId));

        if (!childRequest.getFamily().getUser().getEmail().equals(userEmail)) {
            throw new AccessDeniedException("You cannot request a match for a child that is not yours.");
        }

        ChildEntity childTarget = childRepository.findById(childTargetId)
                .orElseThrow(() -> new ResourceNotFoundException("Target child not found: " + childTargetId));

        UserEntity currentUser = childRequest.getFamily().getUser();
        if (!canBypassVerification(currentUser) && currentUser.getVerificationStatus() != VerificationStatus.VERIFIED) {
            throw new BusinessLogicException("Your account must be VERIFIED to request a match.");
        }

        if (childRequest.getFamily().getNeighborhood() == null || childTarget.getFamily().getNeighborhood() == null) {
            throw new BusinessLogicException("Both families must have a neighborhood assigned to connect.");
        }

        if (!childRequest.getFamily().getNeighborhood().getId().equals(childTarget.getFamily().getNeighborhood().getId())) {
            throw new BusinessLogicException("Connections are only allowed within the same neighborhood.");
        }

        if (blockService.isBlockedEitherWay(childRequest.getFamily().getId(), childTarget.getFamily().getId())) {
            throw new BusinessLogicException("Cannot connect with this family.");
        }

        LocalDateTime oneWeekAgo = LocalDateTime.now().minusWeeks(1);
        return matchRepository.findAll(MatchSpecifications.hasMatchForChildInLastWeek(childRequestId, oneWeekAgo))
                .stream()
                .filter(m -> m.getChildRequest().getId().equals(childTargetId) || m.getChildTarget().getId().equals(childTargetId))
                .findFirst()
                .orElseGet(() -> {
                    MatchEntity savedMatch = matchRepository.save(MatchEntity.builder()
                            .childRequest(childRequest)
                            .childTarget(childTarget)
                            .status(MatchStatus.PENDING)
                            .build());

                    FamilyEntity targetFamily = childTarget.getFamily();
                    notificationService.createInternalNotification(
                            targetFamily,
                            "New connection request!",
                            childRequest.getFamily().getFamilyName() + " wants to connect with your family.",
                            NotificationType.PLAYDATE_REQUEST,
                            savedMatch.getId()
                    );

                    return savedMatch;
                });
    }

    @Override
    public List<FamilyEntity> findCompatibleFamilies(Long neighborhoodId, int minAge, int maxAge, List<Long> interestIds, Long currentChildId, Boolean includePregnant) {
        return List.of();
    }

    @Transactional(readOnly = true)
    @Override
    public List<FamilyEntity> findCompatibleFamilies(Long neighborhoodId, int minAge, int maxAge, List<Long> interestIds, Boolean includePregnant, Long currentChildId) {
        if (neighborhoodId == null) return List.of();

        Long myFamilyId = currentChildId != null ? childRepository.findById(currentChildId).map(c -> c.getFamily().getId()).orElse(null) : null;

        return familyRepository.findAll(Specification.where(FamilySpecifications.hasNeighborhood(neighborhoodId))
                .and(FamilySpecifications.hasChildWithCriteria(minAge, maxAge, interestIds, includePregnant))
                .and(FamilySpecifications.isNotChild(currentChildId))
                .and(FamilySpecifications.isNotMyFamily(myFamilyId)));
    }

    @Override
    @Transactional
    public void respondToMatch(Long matchId, MatchStatus status, String currentUserEmail) {
        MatchEntity match = matchRepository.findById(matchId)
                .orElseThrow(() -> new ResourceNotFoundException("Match not found: " + matchId));

        validateUserInMatch(match, currentUserEmail);

        UserEntity currentUser = (match.getChildTarget().getFamily().getUser().getEmail().equals(currentUserEmail))
                ? match.getChildTarget().getFamily().getUser()
                : match.getChildRequest().getFamily().getUser();

        if (!canBypassVerification(currentUser) && currentUser.getVerificationStatus() != VerificationStatus.VERIFIED) {
            throw new BusinessLogicException("You must be VERIFIED to respond to matches.");
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
        if (weeklyMatchLimit > 0 && countAcceptedMatchesThisWeek(childId) >= weeklyMatchLimit) {
            throw new BusinessLogicException(
                    "You have reached the weekly limit of " + weeklyMatchLimit + " new connections! 🌱 "
                            + "We encourage you to spend time getting to know your current connections. "
                            + "New connections will be available next week.");
        }
    }

    @Override
    public boolean hasActiveMatchThisWeek(Long childId) {
        return countAcceptedMatchesThisWeek(childId) > 0;
    }

    private long countAcceptedMatchesThisWeek(Long childId) {
        if (childId == null) return 0;
        LocalDateTime oneWeekAgo = LocalDateTime.now().minusWeeks(1);
        return matchRepository.count(Specification.where(MatchSpecifications.hasMatchForChildInLastWeek(childId, oneWeekAgo))
                .and((root, query, cb) -> cb.equal(root.get("status"), MatchStatus.ACCEPTED)));
    }

    @Override
    @Transactional(readOnly = true)
    public List<MatchResponseDetailDTO> getMatchesForUser(String email) {
        return Stream.concat(matchRepository.findByChildRequestFamilyUserEmail(email).stream(),
                        matchRepository.findByChildTargetFamilyUserEmail(email).stream())
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
                .theirFamilyId(theirChild.getFamily().getId())
                .theirFamilyName(theirChild.getFamily().getFamilyName())
                .theirNeighborhoodName(theirChild.getFamily().getNeighborhood() != null ? theirChild.getFamily().getNeighborhood().getName() : "N/A")
                .build();
    }

    @Transactional
    @Override
    public void confirmMatch(Long matchId, String userEmail) {
        MatchEntity match = matchRepository.findById(matchId)
                .orElseThrow(() -> new ResourceNotFoundException("Match not found"));

        validateUserInMatch(match, userEmail);

        UserEntity requester = match.getChildRequest().getFamily().getUser();
        UserEntity target = match.getChildTarget().getFamily().getUser();

        if (userEmail.equals(requester.getEmail())) {
            if (!canBypassVerification(requester) && requester.getVerificationStatus() != VerificationStatus.VERIFIED) {
                throw new BusinessLogicException("You must be VERIFIED to confirm a match.");
            }
            match.setUserAccepted(true);
        } else if (userEmail.equals(target.getEmail())) {
            if (!canBypassVerification(target) && target.getVerificationStatus() != VerificationStatus.VERIFIED) {
                throw new BusinessLogicException("You must be VERIFIED to confirm a match.");
            }
            match.setNeighborAccepted(true);
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