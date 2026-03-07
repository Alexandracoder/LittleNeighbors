package com.alexandracoder.littleneighbors.family.repository;

import com.alexandracoder.littleneighbors.family.entity.FamilyEntity;
import com.alexandracoder.littleneighbors.user.entity.UserEntity;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.util.Optional;

public interface FamilyRepository extends JpaRepository<FamilyEntity, Long>,
        JpaSpecificationExecutor<FamilyEntity> {

    @EntityGraph(attributePaths = {
            "neighborhood",
            "neighborhood.city",
            "children",
            "children.interests"
    })
    Optional<FamilyEntity> findWithDetailsById(Long id);
    Optional<FamilyEntity> findByUserEmail(String email);
    boolean existsByUser(UserEntity user);



}

