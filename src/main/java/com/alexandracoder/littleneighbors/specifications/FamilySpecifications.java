package com.alexandracoder.littleneighbors.specifications;

import com.alexandracoder.littleneighbors.child.entity.ChildEntity;
import com.alexandracoder.littleneighbors.family.entity.FamilyEntity;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class FamilySpecifications {
    public static Specification<FamilyEntity> hasNeighborhood(Long neighborhoodId) {
        return  (root,  query,  cb) ->cb.equal(root.get("neighborhood").get("id"), neighborhoodId);
    }

    public static Specification<FamilyEntity> hasChildWithInterest(Long interestId) {
        return (root, query, cb) -> {
            Join<FamilyEntity, ChildEntity> children = root.join("children", JoinType.INNER);
            return cb.isMember(interestId, children.join("interests").get("id"));
        };
    }

    public static Specification<FamilyEntity> hasChildAgeBetween(int minAge, int maxAge) {
        return (root, query, criteriaBuilder) -> {
            Join<FamilyEntity, ChildEntity> children = root.join("children", JoinType.INNER);
            LocalDate today = LocalDate.now();
            LocalDate maxBirthDate = today.minusYears(minAge);
            LocalDate minBirthDate = today.minusYears(maxAge);
            return criteriaBuilder.between(children.get("birthDate"), minBirthDate, maxBirthDate);
        };
    }

}
