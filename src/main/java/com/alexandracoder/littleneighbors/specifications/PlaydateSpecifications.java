package com.alexandracoder.littleneighbors.specifications;

import com.alexandracoder.littleneighbors.playdate.entity.PlaydateEntity;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;

public class PlaydateSpecifications {

    public static Specification<PlaydateEntity> hasUserInMatch(Long userId) {
        return (root, query, cb) -> {

            Join<Object, Object> matchJoin = root.join("match", JoinType.INNER);


            return cb.or(
                    cb.equal(matchJoin.get("childRequest").get("family").get("user").get("id"), userId),
                    cb.equal(matchJoin.get("childTarget").get("family").get("user").get("id"), userId)
            );
        };
    }
}