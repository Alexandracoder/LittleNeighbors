package com.alexandracoder.littleneighbors.specifications;

import com.alexandracoder.littleneighbors.child.entity.ChildEntity;
import com.alexandracoder.littleneighbors.family.entity.FamilyEntity;
import com.alexandracoder.littleneighbors.interest.entity.InterestEntity;
import com.alexandracoder.littleneighbors.match.entity.MatchEntity;
import jakarta.persistence.criteria.*;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;


public class FamilySpecifications {


    public static Specification<FamilyEntity> fetchAll() {
        return (root, query, cb) -> {
            if (Long.class != query.getResultType()) {

                Fetch<FamilyEntity, ?> childrenFetch = root.fetch("children", JoinType.LEFT);
                childrenFetch.fetch("interests", JoinType.LEFT);
                root.fetch("neighborhood", JoinType.LEFT);
            }
            return cb.conjunction();
        };
    }

    public static Specification<FamilyEntity> hasNeighborhood(Long neighborhoodId) {
        if (neighborhoodId == null) return null;
        return (root, query, cb) -> cb.equal(root.get("neighborhood").get("id"), neighborhoodId);
    }

    public static Specification<FamilyEntity> hasChildWithCriteria(int minAge, int maxAge, List<Long> interestIds) {
        return (root, query, cb) -> {
            Subquery<Long> sub = query.subquery(Long.class);
            Root<ChildEntity> child = sub.from(ChildEntity.class);
            sub.select(child.get("id"));

            LocalDate today = LocalDate.now();
            LocalDate maxBirthDate = today.minusYears(minAge);
            LocalDate minBirthDate = today.minusYears(maxAge).minusYears(1).plusDays(1);

            Predicate belongsToFamily = cb.equal(child.get("family"), root);
            Predicate agePredicate = cb.between(child.get("birthDate"), minBirthDate, maxBirthDate);

            if (interestIds != null && !interestIds.isEmpty()) {
                Join<ChildEntity, InterestEntity> interests = child.join("interests", JoinType.INNER);
                sub.where(cb.and(belongsToFamily, agePredicate, interests.get("id").in(interestIds)));
            } else {
                sub.where(cb.and(belongsToFamily, agePredicate));
            }
            return cb.exists(sub);
        };
    }

    public static Specification<FamilyEntity> hasNoRecentMatch(LocalDateTime since) {
        return (root, query, cb) -> {
            if (since == null) return null;
            query.distinct(true);

            Subquery<Long> subquery = query.subquery(Long.class);
            Root<MatchEntity> matchRoot = subquery.from(MatchEntity.class);

            subquery.select(matchRoot.get("id"))
                    .where(cb.and(
                            cb.or(
                                    cb.equal(matchRoot.get("childA").get("family").get("id"), root.get("id")),
                                    cb.equal(matchRoot.get("childB").get("family").get("id"), root.get("id"))
                            ),
                            cb.greaterThan(matchRoot.get("createdAt"), since)
                    ));

            return cb.not(cb.exists(subquery));
        };
    }

    public static Specification<FamilyEntity> isNotMyFamily(Long myFamilyId) {
        if (myFamilyId == null) return null;
        return (root, query, cb) -> {
            query.distinct(true);
            return cb.notEqual(root.get("id"), myFamilyId);
        };
    }

    public static Specification<FamilyEntity> isNotChild(Long currentChildId) {
        if (currentChildId == null) return null;
        return (root, query, cb) -> {
            query.distinct(true);
            Subquery<Long> sub = query.subquery(Long.class);
            Root<ChildEntity> child = sub.from(ChildEntity.class);
            sub.select(child.get("id"))
                    .where(cb.and(
                            cb.equal(child.get("family"), root),
                            cb.equal(child.get("id"), currentChildId)
                    ));
            return cb.not(cb.exists(sub));
        };
    }

    public static Specification<FamilyEntity> hasNeighborhoodId(Long neighborhoodId) {
        return (root, query, cb) -> {
            if (neighborhoodId == null) return cb.conjunction();
            return cb.equal(root.get("neighborhood").get("id"), neighborhoodId);
        };
    }
    public static Specification<FamilyEntity> fetchEverything() {
        return (root, query, cb) -> {

            if (Long.class != query.getResultType()) {

                root.fetch("user", JoinType.INNER);
                root.fetch("neighborhood", JoinType.LEFT);
                root.fetch("children", JoinType.LEFT).fetch("interests", JoinType.LEFT);
            }
            return cb.conjunction();
        };
    }
}