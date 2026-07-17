package com.alexandracoder.littleneighbors.block.controller;

import com.alexandracoder.littleneighbors.block.dto.BlockedFamilySummaryDTO;
import com.alexandracoder.littleneighbors.block.service.BlockService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/families/blocks")
@RequiredArgsConstructor
public class BlockController {

    private final BlockService blockService;

    @PostMapping("/{familyId}")
    @PreAuthorize("hasRole('FAMILY')")
    public ResponseEntity<Void> blockFamily(@PathVariable Long familyId, Principal principal) {
        blockService.blockFamily(principal.getName(), familyId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{familyId}")
    @PreAuthorize("hasRole('FAMILY')")
    public ResponseEntity<Void> unblockFamily(@PathVariable Long familyId, Principal principal) {
        blockService.unblockFamily(principal.getName(), familyId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    @PreAuthorize("hasRole('FAMILY')")
    public ResponseEntity<List<BlockedFamilySummaryDTO>> listBlocked(Principal principal) {
        return ResponseEntity.ok(blockService.listBlockedByMe(principal.getName()));
    }
}
