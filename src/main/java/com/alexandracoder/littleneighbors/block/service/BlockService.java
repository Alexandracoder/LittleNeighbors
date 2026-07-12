package com.alexandracoder.littleneighbors.block.service;

import com.alexandracoder.littleneighbors.block.dto.BlockedFamilySummaryDTO;

import java.util.List;

public interface BlockService {

    void blockFamily(String blockerEmail, Long blockedFamilyId);

    void unblockFamily(String blockerEmail, Long blockedFamilyId);

    List<BlockedFamilySummaryDTO> listBlockedByMe(String blockerEmail);

    /**
     * true si hay bloqueo en CUALQUIER sentido entre las dos familias
     * (A bloqueó a B, o B bloqueó a A) — el contacto debe quedar cortado
     * para ambas partes por igual, sin importar quién bloqueó a quién.
     */
    boolean isBlockedEitherWay(Long familyIdA, Long familyIdB);

    /**
     * IDs de todas las familias bloqueadas (en cualquier sentido) que
     * involucran a esta familia. Útil para excluir de golpe del listado
     * de Explorar sin hacer una consulta por cada candidata.
     */
    List<Long> getBlockedFamilyIdsInvolving(Long familyId);
}
