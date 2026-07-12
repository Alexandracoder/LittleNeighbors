package com.alexandracoder.littleneighbors.block.service;

import com.alexandracoder.littleneighbors.block.dto.BlockedFamilySummaryDTO;
import com.alexandracoder.littleneighbors.block.entity.BlockedFamilyEntity;
import com.alexandracoder.littleneighbors.block.repository.BlockedFamilyRepository;
import com.alexandracoder.littleneighbors.family.entity.FamilyEntity;
import com.alexandracoder.littleneighbors.family.repository.FamilyRepository;
import com.alexandracoder.littleneighbors.shared.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BlockServiceImpl implements BlockService {

    private final BlockedFamilyRepository blockedFamilyRepository;
    private final FamilyRepository familyRepository;

    @Override
    @Transactional
    public void blockFamily(String blockerEmail, Long blockedFamilyId) {
        FamilyEntity blocker = familyRepository.findByUserEmail(blockerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Family not found for current user"));

        if (blocker.getId().equals(blockedFamilyId)) {
            throw new IllegalStateException("You cannot block your own family");
        }

        FamilyEntity blocked = familyRepository.findById(blockedFamilyId)
                .orElseThrow(() -> new ResourceNotFoundException("Family to block not found"));

        boolean alreadyBlocked = blockedFamilyRepository
                .existsByBlockerFamily_IdAndBlockedFamily_Id(blocker.getId(), blocked.getId());
        if (alreadyBlocked) {
            return; // idempotente: bloquear dos veces no es un error
        }

        BlockedFamilyEntity entity = BlockedFamilyEntity.builder()
                .blockerFamily(blocker)
                .blockedFamily(blocked)
                .build();

        blockedFamilyRepository.save(entity);
    }

    @Override
    @Transactional
    public void unblockFamily(String blockerEmail, Long blockedFamilyId) {
        FamilyEntity blocker = familyRepository.findByUserEmail(blockerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Family not found for current user"));

        blockedFamilyRepository
                .findByBlockerFamily_IdAndBlockedFamily_Id(blocker.getId(), blockedFamilyId)
                .ifPresent(blockedFamilyRepository::delete);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BlockedFamilySummaryDTO> listBlockedByMe(String blockerEmail) {
        FamilyEntity blocker = familyRepository.findByUserEmail(blockerEmail)
                .orElseThrow(() -> new ResourceNotFoundException("Family not found for current user"));

        return blockedFamilyRepository.findByBlockerFamily_Id(blocker.getId()).stream()
                .map(b -> new BlockedFamilySummaryDTO(
                        b.getBlockedFamily().getId(),
                        b.getBlockedFamily().getFamilyName(),
                        b.getCreatedAt()
                ))
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isBlockedEitherWay(Long familyIdA, Long familyIdB) {
        if (familyIdA == null || familyIdB == null) return false;
        return blockedFamilyRepository
                .existsByBlockerFamily_IdAndBlockedFamily_IdOrBlockerFamily_IdAndBlockedFamily_Id(
                        familyIdA, familyIdB, familyIdB, familyIdA);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Long> getBlockedFamilyIdsInvolving(Long familyId) {
        if (familyId == null) return List.of();

        return blockedFamilyRepository.findByBlockerFamily_IdOrBlockedFamily_Id(familyId, familyId).stream()
                .map(b -> b.getBlockerFamily().getId().equals(familyId)
                        ? b.getBlockedFamily().getId()
                        : b.getBlockerFamily().getId())
                .collect(Collectors.toList());
    }
}
