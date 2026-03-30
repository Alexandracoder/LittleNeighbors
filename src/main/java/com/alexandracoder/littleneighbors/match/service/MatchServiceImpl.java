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
            throw new BusinessLogicException("No puedes solicitar un match con el mismo niño.");
        }

        ChildEntity childA = childRepository.findById(childAId)
                .orElseThrow(() -> new ResourceNotFoundException("Niño iniciador no encontrado: " + childAId));
        ChildEntity childB = childRepository.findById(childBId)
                .orElseThrow(() -> new ResourceNotFoundException("Niño destino no encontrado: " + childBId));

        validateWeeklyConstraint(childAId);

        if (childA.getFamily().getNeighborhood() == null || childB.getFamily().getNeighborhood() == null) {
            throw new BusinessLogicException("Ambas familias deben tener un barrio asignado para conectar.");
        }

        if (!childA.getFamily().getNeighborhood().getId().equals(childB.getFamily().getNeighborhood().getId())) {
            throw new BusinessLogicException("Solo se permiten conexiones dentro del mismo barrio.");
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
            throw new BusinessLogicException("Solo se permite una solicitud de match por semana.");
        }
    }

    @Override
    public boolean hasActiveMatchThisWeek(Long childId) {
        if (childId == null) return false;
        LocalDateTime oneWeekAgo = LocalDateTime.now().minusWeeks(1);

        return matchRepository.exists(MatchSpecifications.hasMatchForChildInLastWeek(childId, oneWeekAgo));
    }
}
