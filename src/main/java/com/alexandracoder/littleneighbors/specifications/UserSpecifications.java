package com.alexandracoder.littleneighbors.specifications;

import com.alexandracoder.littleneighbors.user.entity.UserEntity;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

public class UserSpecifications {

    public static Specification<UserEntity> hasEmailWithFullProfile(String email) {
        return (root, query, cb) -> {

            if (query.getResultType() != Long.class && query.getResultType() != long.class) {


                var familyFetch = root.fetch("family", JoinType.LEFT);
                var neighborhoodFetch = familyFetch.fetch("neighborhood", JoinType.LEFT);
                neighborhoodFetch.fetch("city", JoinType.LEFT);

            }

            return cb.equal(root.get("email"), email);
        };
    }
}