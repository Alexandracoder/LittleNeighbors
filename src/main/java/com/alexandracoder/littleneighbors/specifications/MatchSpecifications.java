package com.alexandracoder.littleneighbors.specifications;

import com.alexandracoder.littleneighbors.child.entity.ChildEntity;
import com.alexandracoder.littleneighbors.enums.MatchStatus;
import com.alexandracoder.littleneighbors.match.entity.MatchEntity;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;
import java.time.LocalDateTime;

public class MatchSpecifications {

    public static Specification<MatchEntity> hasMatchForChildInLastWeek(Long childId, LocalDateTime after) {
        return (root, query, cb) -> {
            if (childId == null || after == null) return null;


            Join<MatchEntity, ChildEntity> requestJoin = root.join("childRequest", JoinType.INNER);
            Join<MatchEntity, ChildEntity> targetJoin = root.join("childTarget", JoinType.INNER);

            Predicate isRequestor = cb.equal(requestJoin.get("id"), childId);
            Predicate isTarget = cb.equal(targetJoin.get("id"), childId);

            return cb.and(cb.or(isRequestor, isTarget), cb.greaterThan(root.get("createdAt"), after));
        };
    }

    public static Specification<MatchEntity> isAccepted() {
        return (root, query, cb) -> cb.equal(root.get("status"), MatchStatus.ACCEPTED);
    }

    public static Specification<MatchEntity> belongsToNeighborhood(Long neighborhoodId) {
        return (root, query, cb) -> {
            if (neighborhoodId == null) return cb.conjunction();


            root.fetch("initiatorFamily", JoinType.LEFT).fetch("neighborhood", JoinType.LEFT);
            root.fetch("targetFamily", JoinType.LEFT).fetch("neighborhood", JoinType.LEFT);

            return cb.or(
                    cb.equal(root.get("initiatorFamily").get("neighborhood").get("id"), neighborhoodId),
                    cb.equal(root.get("targetFamily").get("neighborhood").get("id"), neighborhoodId)
            );
        };
    }
}