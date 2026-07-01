package com.alexandracoder.littleneighbors.user.repository;

import com.alexandracoder.littleneighbors.enums.VerificationStatus;
import com.alexandracoder.littleneighbors.user.entity.UserEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, Long>, JpaSpecificationExecutor<UserEntity> {

    Optional<UserEntity> findByEmail(String email);

    boolean existsByEmail(String email);

    @EntityGraph(attributePaths = {
            "family",
            "family.neighborhood",
            "family.neighborhood.city",
            "family.children",
            "family.children.interests"
    })
    List<UserEntity> findByVerificationStatus(VerificationStatus status);

    Optional<UserEntity> findByResetPasswordToken(String token);
}