package com.alexandracoder.littleneighbors.specifications;

import com.alexandracoder.littleneighbors.neighborhood.entity.NeighborhoodEntity;
import org.springframework.data.jpa.domain.Specification;

public class NeighborhoodSpecifications {

    public static Specification<NeighborhoodEntity> hasName(String name) {
        return (root, query, criteriaBuilder) -> {
            if (name == null || name.isEmpty()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(
                    criteriaBuilder.lower(root.get("name")),
                    name.toLowerCase()
            );
        };
    }
    public static Specification<NeighborhoodEntity> hasCityId(Long cityId) {
        return (root, query, criteriaBuilder) -> {
            if (cityId == null) {
                return criteriaBuilder.conjunction();
            }

            return criteriaBuilder.equal(root.get("city").get("id"), cityId);
        };
    }
}
