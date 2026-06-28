package com.alexandracoder.littleneighbors.specifications;

import com.alexandracoder.littleneighbors.neighborhood.entity.NeighborhoodEntity;
import org.springframework.data.jpa.domain.Specification;

public class NeighborhoodSpecifications {
    public static Specification<NeighborhoodEntity> hasName(String name) {
        return (root, query, cb) ->
                name == null ? null : cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }

    public static Specification<NeighborhoodEntity> hasCityId(Long cityId) {
        return (root, query, cb) ->
                cityId == null ? null : cb.equal(root.get("city").get("id"), cityId);
    }
}