package com.alexandracoder.littleneighbors.specifications;

import com.alexandracoder.littleneighbors.match.entity.MatchEntity;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;
import java.time.LocalDateTime;

public class MatchSpecifications {

    public static Specification<MatchEntity> hasMatchForChildInLastWeek(Long childId, LocalDateTime after) {
        return (root, query, cb) -> {
            if (childId == null || after == null) return null;

            Predicate isRequestor = cb.equal(root.get("childRequest").get("id"), childId);
            Predicate isTarget = cb.equal(root.get("childTarget").get("id"), childId);

            Predicate participatingInMatch = cb.or(isRequestor, isTarget);
            Predicate isRecent = cb.greaterThan(root.get("createdAt"), after);

            return cb.and(participatingInMatch, isRecent);
        };
    }
}