package com.alexandracoder.littleneighbors.message.repository;

import com.alexandracoder.littleneighbors.message.entity.MessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<MessageEntity, Long> {
    // Para cargar el historial del chat
    List<MessageEntity> findByMatchIdOrderBySentAtAsc(Long matchId);
}
