package com.alexandracoder.littleneighbors.qr.repository;


import com.alexandracoder.littleneighbors.qr.entity.QrEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface QrRepository extends JpaRepository<QrEntity, Long>, JpaSpecificationExecutor<QrEntity> {

    boolean existsByEmailAndNeighborhood(String email, String neighborhood);

    long countByNeighborhood(String neighborhood);

    Optional<QrEntity> findByInviteToken(String inviteToken);

    long countByNeighborhoodAndConvertedAtIsNotNull(String neighborhood);

    List<QrEntity> findByConvertedAtIsNullAndAnonymizedFalseAndCreatedAtBefore(LocalDateTime cutoff);
}