package com.alexandracoder.littleneighbors.specifications;

import com.alexandracoder.littleneighbors.qr.entity.QrEntity;
import org.springframework.data.jpa.domain.Specification;

public class QrSpecifications {

    public static Specification<QrEntity> hasNeighborhood(String neighborhood) {
        return (root, query, criteriaBuilder) -> {
            if (neighborhood == null || neighborhood.isBlank()) return criteriaBuilder.conjunction();
            return criteriaBuilder.equal(
                    criteriaBuilder.lower(root.get("neighborhood")),
                    neighborhood.trim().toLowerCase()
            );
        };
    }

    public static Specification<QrEntity> hasEmail(String email) {
        return (root, query, criteriaBuilder) -> {
            if (email == null || email.isBlank()) return criteriaBuilder.conjunction();
            return criteriaBuilder.equal(root.get("email"), email.trim().toLowerCase());
        };
    }
}