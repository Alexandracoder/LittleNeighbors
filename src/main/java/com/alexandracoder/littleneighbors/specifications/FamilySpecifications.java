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

    public static Specification<FamilyEntity> hasNeighborhood(Long neighborhoodId) {
        if (neighborhoodId == null) return null;
        return (root, query, cb) -> cb.equal(root.get("neighborhood").get("id"), neighborhoodId);
    }

    public static Specification<FamilyEntity> hasChildWithCriteria(int minAge, int maxAge, List<Long> interestIds) {
        return (root, query, cb) -> {
            query.distinct(true);
            Join<FamilyEntity, ChildEntity> children = root.join("children", JoinType.INNER);

            LocalDate today = LocalDate.now();
            LocalDate maxBirthDate = today.minusYears(minAge);
            LocalDate minBirthDate = today.minusYears(maxAge).minusYears(1).plusDays(1);

            Predicate agePredicate = cb.between(children.get("birthDate"), minBirthDate, maxBirthDate);

            if (interestIds != null && !interestIds.isEmpty()) {
                Join<ChildEntity, InterestEntity> interests = children.join("interests", JoinType.INNER);
                return cb.and(agePredicate, interests.get("id").in(interestIds));
            }
            return agePredicate;
        };
    }

    public static Specification<FamilyEntity> hasNoRecentMatch(LocalDateTime since) {
        return (root, query, cb) -> {
            if (since == null) return null;

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

    public static Specification<FamilyEntity> hasChildAgeBetween(int minAge, int maxAge) {
        return (root, query, cb) -> {
            query.distinct(true);
            LocalDate today = LocalDate.now();
            LocalDate maxBirthDate = today.minusYears(minAge);
            LocalDate minBirthDate = today.minusYears(maxAge).minusYears(1).plusDays(1);

            return cb.between(root.join("children").get("birthDate"), minBirthDate, maxBirthDate);
        };
    }

    public static Specification<FamilyEntity> isNotMyFamily(Long myFamilyId) {
        if (myFamilyId == null) return null;
        return (root, query, cb) -> cb.notEqual(root.get("id"), myFamilyId);
    }

    public static Specification<FamilyEntity> isNotChild(Long currentChildId) {
        if (currentChildId == null) return null;
        return (root, query, cb) -> {
            query.distinct(true);
            return cb.notEqual(root.join("children").get("id"), currentChildId);
        };
    }

    public static Specification<FamilyEntity> hasChildWithInterest(List<Long> interestIds) {
        if (interestIds == null || interestIds.isEmpty()) return null;
        return (root, query, cb) -> {
            query.distinct(true);
            return root.join("children")
                    .join("interests")
                    .get("id")
                    .in(interestIds);
        };
    }
}