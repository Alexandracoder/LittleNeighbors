package com.alexandracoder.littleneighbors.specifications;

import com.alexandracoder.littleneighbors.match.entity.MatchEntity;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;
import java.time.LocalDateTime;

public class MatchSpecifications {

    public static Specification<MatchEntity> hasMatchForChildInLastWeek(Long childId, LocalDateTime after) {
        return (root, query, cb) -> {

            Predicate childA = cb.equal(root.get("childA").get("id"), childId);
            Predicate childB = cb.equal(root.get("childB").get("id"), childId);
            Predicate childOr = cb.or(childA, childB);

            Predicate dateAfter = cb.greaterThan(root.get("createdAt"), after);

            return cb.and(childOr, dateAfter);
        };
    }
}