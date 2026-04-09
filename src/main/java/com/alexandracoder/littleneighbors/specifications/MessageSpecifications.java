package com.alexandracoder.littleneighbors.specifications;

import com.alexandracoder.littleneighbors.message.entity.MessageEntity;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;

public class MessageSpecifications {

    public static Specification<MessageEntity> isConversationBetween(Long fam1Id, Long fam2Id) {
        return (root, query, cb) -> {
            if (fam1Id == null || fam2Id == null) return null;

            Predicate f1ToF2 = cb.and(
                    cb.equal(root.get("sender").get("family").get("id"), fam1Id),
                    cb.equal(root.get("receiver").get("family").get("id"), fam2Id)
            );


            Predicate f2ToF1 = cb.and(
                    cb.equal(root.get("sender").get("family").get("id"), fam2Id),
                    cb.equal(root.get("receiver").get("family").get("id"), fam1Id)
            );

            query.orderBy(cb.asc(root.get("sentAt")));
            return cb.or(f1ToF2, f2ToF1);
        };
    }

    public static Specification<MessageEntity> hasMatchId(Long matchId) {
        return (root, query, cb) -> {
            if (matchId == null) return null;
            return cb.equal(root.get("match").get("id"), matchId);
        };
    }
}