package com.alexandracoder.littleneighbors.specifications;

import com.alexandracoder.littleneighbors.event.entity.EventEntity;
import org.springframework.data.jpa.domain.Specification;

public class EventSpecifications {

    public static Specification<EventEntity> withinBoundingBox(
            Double minLat, Double maxLat, Double minLon, Double maxLon) {
        return (root, query, cb) -> cb.and(
                cb.between(root.get("latitude"), minLat, maxLat),
                cb.between(root.get("longitude"), minLon, maxLon)
        );
    }

    public static Specification<EventEntity> isUpcoming() {
        return (root, query, cb) -> cb.greaterThan(root.get("eventDate"), java.time.LocalDateTime.now());
    }

    public static Specification<EventEntity> inNeighborhood(Long neighborhoodId) {
        if (neighborhoodId == null) return null;
        return (root, query, cb) -> cb.equal(root.get("neighborhood").get("id"), neighborhoodId);
    }
}