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
        // 1. Evitar que una familia se haga match a sí misma
        if (childAId.equals(childBId)) {
            throw new IllegalStateException("No puedes solicitar un match contigo mismo.");
        }

        // 2. Validaciones de existencia
        ChildEntity childA = childRepository.findById(childAId)
                .orElseThrow(() -> new RuntimeException("Niño iniciador no encontrado (ID: " + childAId + ")"));
        ChildEntity childB = childRepository.findById(childBId)
                .orElseThrow(() -> new RuntimeException("Niño destino no encontrado (ID: " + childBId + ")"));

        // 3. Validación semanal: Solo para el iniciador (Child A)
        // El que "gasta" su oportunidad semanal es el que pulsa el botón.
        validateWeeklyConstraint(childAId);

        // 4. Validación de barrio (Mismo Neighborhood)
        Long neighborhoodA = childA.getFamily().getNeighborhood().getId();
        Long neighborhoodB = childB.getFamily().getNeighborhood().getId();

        if (!neighborhoodA.equals(neighborhoodB)) {
            throw new IllegalStateException("Solo puedes conectar con familias de tu mismo barrio.");
        }

        // 5. Guardar Match PENDING
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

        // 1. Filtros básicos: Barrio y Edad
        Specification<FamilyEntity> spec = Specification
                .where(FamilySpecifications.hasNeighborhood(neighborhoodId))
                .and(FamilySpecifications.hasChildAgeBetween(minAge, maxAge));

        // 2. Filtro de intereses (opcional)
        if (interestIds != null && !interestIds.isEmpty()) {
            spec = spec.and(FamilySpecifications.hasChildWithInterest(interestIds));
        }

        // 3. Filtro de exclusión: No mostrar familias que ya han tenido un match reciente
        // Esto asegura que el Explorer siempre esté "fresco" con gente disponible
        spec = spec.and(FamilySpecifications.hasNoRecentMatch(oneWeekAgo));

        return familyRepository.findAll(spec);
    }

    @Override
    public void validateWeeklyConstraint(Long childId) {
        LocalDateTime oneWeekAgo = LocalDateTime.now().minusWeeks(1);

        // Si no quieres usar métodos de repo, puedes usar un count con Specification aquí también
        // Pero para un simple exists, el repo suele ser suficiente.
        // Si el repo te da problemas con el OR, asegúrate de que el nombre sea exacto:
        boolean hasMatch = matchRepository.existsByChildAIdOrChildBIdAndCreatedAtAfter(
                childId, childId, oneWeekAgo);

        if (hasMatch) {
            throw new IllegalStateException("¡Paciencia! Solo se permite un match por semana para asegurar conexiones reales.");
        }
    }

    @Override
    public boolean hasActiveMatchThisWeek(Long childId) {
        LocalDateTime oneWeekAgo = LocalDateTime.now().minusWeeks(1);

        // Usamos el mismo método de tu repositorio que ya tienes en validateWeeklyConstraint
        return matchRepository.existsByChildAIdOrChildBIdAndCreatedAtAfter(
                childId,
                childId,
                oneWeekAgo
        );
    }
}
