package com.alexandracoder.littleneighbors.notification.repository;

import com.alexandracoder.littleneighbors.notification.entity.NotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<NotificationEntity, Long>,
        JpaSpecificationExecutor<NotificationEntity> {
    List<NotificationEntity> findByRecipientFamily_User_EmailOrderByCreatedAtDesc(String email);

    long countByRecipientFamily_User_EmailAndIsReadFalse(String email);
}