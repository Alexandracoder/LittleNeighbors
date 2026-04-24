package com.alexandracoder.littleneighbors.specifications;

import com.alexandracoder.littleneighbors.notification.entity.NotificationEntity;
import org.springframework.data.jpa.domain.Specification;

public class NotificationSpecifications {

    public static Specification<NotificationEntity> hasRecipientFamily(Long familyId) {
        return (root, query, cb) -> cb.equal(root.get("recipientFamily").get("id"), familyId);
    }

    public static Specification<NotificationEntity> isUnread() {
        return (root, query, cb) -> cb.equal(root.get("isRead"), false);
    }
}