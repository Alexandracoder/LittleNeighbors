package com.alexandracoder.littleneighbors.message.repository;

import com.alexandracoder.littleneighbors.message.entity.MessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<MessageEntity, Long>, JpaSpecificationExecutor<MessageEntity> {
    List<MessageEntity> findByMatchIdOrderBySentAtAsc(Long matchId);
}