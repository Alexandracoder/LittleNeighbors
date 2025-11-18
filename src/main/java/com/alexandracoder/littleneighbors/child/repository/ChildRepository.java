package com.alexandracoder.littleneighbors.child.repository;

import com.alexandracoder.littleneighbors.child.entity.ChildEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChildRepository extends JpaRepository<ChildEntity, Long> {
}