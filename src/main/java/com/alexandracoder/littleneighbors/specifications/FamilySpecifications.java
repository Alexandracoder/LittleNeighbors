package com.alexandracoder.littleneighbors.specifications;

import com.alexandracoder.littleneighbors.child.entity.ChildEntity;
import com.alexandracoder.littleneighbors.family.entity.FamilyEntity;
import com.alexandracoder.littleneighbors.interest.entity.InterestEntity;
import com.alexandracoder.littleneighbors.match.entity.MatchEntity;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public class FamilySpecifications {

    public static Specification<FamilyEntity> hasNeighborhood(Long neighborhoodId) {
        if (neighborhoodId == null) return null;
        return (root, query, cb) -> cb.equal(root.get("neighborhood").get("id"), neighborhoodId);
    }

    public static Specification<FamilyEntity> hasChildWithInterest(List<Long> interestIds) {
        if (interestIds == null || interestIds.isEmpty()) return null;
        return (root, query, cb) -> {
            query.distinct(true);
            Join<FamilyEntity, ChildEntity> children = root.join("children", JoinType.INNER);
            Join<ChildEntity, InterestEntity> interests = children.join("interests", JoinType.INNER);

            return interests.get("id").in(interestIds);
        };
    }

    public static Specification<FamilyEntity> hasChildAgeBetween(int minAge, int maxAge) {
        return (root, query, cb) -> {
            query.distinct(true);
            Join<FamilyEntity, ChildEntity> children = root.join("children", JoinType.INNER);

            LocalDate today = LocalDate.now();

            LocalDate maxBirthDate = today.minusYears(minAge);
            LocalDate minBirthDate = today.minusYears(maxAge);

            return cb.between(children.get("birthDate"), minBirthDate, maxBirthDate);
        };
    }


    public static Specification<FamilyEntity> hasNoRecentMatch(LocalDateTime since) {
        return (root, query, cb) -> {

            Subquery<Integer> subquery = query.subquery(Integer.class);
            Root<MatchEntity> matchRoot = subquery.from(MatchEntity.class);

            subquery.select(cb.literal(1))
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

        public static Specification<FamilyEntity> fetchFullFamilyByEmail(String email) {
            return (root, query, cb) -> {

                if (query.getResultType() != Long.class && query.getResultType() != long.class) {
                    root.fetch("neighborhood", JoinType.LEFT)
                            .fetch("city", JoinType.LEFT);

                    root.fetch("children", JoinType.LEFT)
                            .fetch("interests", JoinType.LEFT);
                }
                return cb.equal(root.get("user").get("email"), email);
            };
        }
    }