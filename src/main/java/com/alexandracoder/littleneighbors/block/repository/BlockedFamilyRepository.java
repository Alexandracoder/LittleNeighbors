package com.alexandracoder.littleneighbors.block.repository;

import com.alexandracoder.littleneighbors.block.entity.BlockedFamilyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BlockedFamilyRepository extends JpaRepository<BlockedFamilyEntity, Long> {

    boolean existsByBlockerFamily_IdAndBlockedFamily_Id(Long blockerFamilyId, Long blockedFamilyId);

    // Bloqueo en cualquiera de los dos sentidos: si A bloqueó a B, o B bloqueó
    // a A, el contacto debe quedar cortado para ambos por igual.
    boolean existsByBlockerFamily_IdAndBlockedFamily_IdOrBlockerFamily_IdAndBlockedFamily_Id(
            Long blockerA, Long blockedA, Long blockerB, Long blockedB);

    List<BlockedFamilyEntity> findByBlockerFamily_Id(Long blockerFamilyId);

    Optional<BlockedFamilyEntity> findByBlockerFamily_IdAndBlockedFamily_Id(Long blockerFamilyId, Long blockedFamilyId);

    // Todos los pares (en cualquier sentido) que involucran a esta familia,
    // para poder excluir de golpe del listado de Explorar.
    List<BlockedFamilyEntity> findByBlockerFamily_IdOrBlockedFamily_Id(Long familyIdAsBlocker, Long familyIdAsBlocked);
}
