package com.alexandracoder.littleneighbors.specifications;

import com.alexandracoder.littleneighbors.child.entity.ChildEntity;
import com.alexandracoder.littleneighbors.family.entity.FamilyEntity;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.List;

public class FamilySpecifications {

    public static Specification<FamilyEntity> hasNeighborhood(Long neighborhoodId) {
        return (root, query, cb) -> cb.equal(root.get("neighborhood").get("id"), neighborhoodId);
    }

    public static Specification<FamilyEntity> hasChildWithInterest(List<Long> interestIds) {
        return (root, query, cb) -> {
            query.distinct(true);
            Join<Object, Object> children = root.join("children", JoinType.INNER);
            Join<Object, Object> interests = children.join("interests", JoinType.INNER);
            // Usamos .in() para la colección de IDs
            return interests.get("id").in(interestIds);
        };
    }

    public static Specification<FamilyEntity> hasChildAgeBetween(int minAge, int maxAge) {
        return (root, query, cb) -> {
            query.distinct(true);
            Join<FamilyEntity, ChildEntity> children = root.join("children", JoinType.INNER);
            LocalDate today = LocalDate.now();
            // La lógica de fechas es correcta:
            // minAge 2 -> nace hace máximo 2 años (maxBirthDate)
            // maxAge 5 -> nace hace mínimo 5 años (minBirthDate)
            LocalDate maxBirthDate = today.minusYears(minAge);
            LocalDate minBirthDate = today.minusYears(maxAge);
            return cb.between(children.get("birthDate"), minBirthDate, maxBirthDate);
        };
    }
}