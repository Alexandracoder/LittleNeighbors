package com.alexandracoder.littleneighbors.match.service;

import com.alexandracoder.littleneighbors.child.entity.ChildEntity;
import com.alexandracoder.littleneighbors.child.repository.ChildRepository;
import com.alexandracoder.littleneighbors.enums.MatchStatus;
import com.alexandracoder.littleneighbors.family.entity.FamilyEntity;
import com.alexandracoder.littleneighbors.family.repository.FamilyRepository;
import com.alexandracoder.littleneighbors.match.entity.MatchEntity;
import com.alexandracoder.littleneighbors.match.repository.MatchRepository;
import com.alexandracoder.littleneighbors.specifications.FamilySpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
        // Validamos a ambos niños para garantizar la restricción semanal
        validateWeeklyConstraint(childAId);
        validateWeeklyConstraint(childBId);

        ChildEntity childA = childRepository.findById(childAId)
                .orElseThrow(() -> new RuntimeException("Child A not found"));
        ChildEntity childB = childRepository.findById(childBId)
                .orElseThrow(() -> new RuntimeException("Child B not found"));

        // Validación de barrio: deben ser del mismo neighborhood
        if (!childA.getFamily().getNeighborhood().getId().equals(childB.getFamily().getNeighborhood().getId())) {
            throw new IllegalStateException("Matches are only allowed within the same neighborhood.");
        }

        return matchRepository.save(MatchEntity.builder()
                .childA(childA)
                .childB(childB)
                .status(MatchStatus.PENDING)
                .build());
    }

    @Override
    public List<FamilyEntity> findCompatibleFamilies(Long neighborhoodId, int minAge, int maxAge, List<Long> interestId) {
        List<Specification<FamilyEntity>> specs = new ArrayList<>();

        specs.add(FamilySpecifications.hasNeighborhood(neighborhoodId));
        specs.add(FamilySpecifications.hasChildAgeBetween(minAge, maxAge));

        if (interestId != null) {
            specs.add(FamilySpecifications.hasChildWithInterest(interestId));
        }

        return familyRepository.findAll(Specification.allOf(specs));
    }

    @Override
    public void validateWeeklyConstraint(Long childId) {
        LocalDateTime oneWeekAgo = LocalDateTime.now().minusWeeks(1);
        // Comprobamos si el niño ya ha participado en un match esta semana
        boolean hasRecentMatch = matchRepository.existsByChildAIdOrChildBIdAndCreatedAtAfter(
                childId, childId, oneWeekAgo);

        if (hasRecentMatch) {
            throw new IllegalStateException("You can only request one match per week.");
        }
    }
}
