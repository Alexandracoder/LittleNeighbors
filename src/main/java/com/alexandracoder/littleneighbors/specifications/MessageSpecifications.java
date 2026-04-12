package com.alexandracoder.littleneighbors.specifications;

import com.alexandracoder.littleneighbors.message.entity.MessageEntity;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;

public class MessageSpecifications {

    public static Specification<MessageEntity> isConversationBetween(Long userId1, Long userId2) {
        return (root, query, cb) -> {
            if (userId1 == null || userId2 == null) return cb.disjunction();

            Predicate caseA = cb.and(
                    cb.equal(root.get("sender").get("id"), userId1),
                    cb.equal(root.get("receiver").get("id"), userId2)
            );

            Predicate caseB = cb.and(
                    cb.equal(root.get("sender").get("id"), userId2),
                    cb.equal(root.get("receiver").get("id"), userId1)
            );

            if (query.getResultType() != Long.class && query.getResultType() != long.class) {
                query.orderBy(cb.asc(root.get("sentAt")));
            }

            return cb.or(caseA, caseB);
        };
    }

    public static Specification<MessageEntity> hasMatchId(Long matchId) {
        return (root, query, cb) -> {
            if (matchId == null) return null;

            if (query.getResultType() != Long.class && query.getResultType() != long.class) {
                query.orderBy(cb.asc(root.get("sentAt")));
            }

            return cb.equal(root.get("match").get("id"), matchId);
        };
    }
}