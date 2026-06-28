package com.alexandracoder.littleneighbors.shared.ratelimit;

import org.springframework.stereotype.Service;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class InMemoryRateLimiterService implements RateLimiterService {

    private record Window(AtomicInteger count, Instant windowStart) {}

    private final ConcurrentHashMap<String, Window> windows = new ConcurrentHashMap<>();

    @Override
    public boolean isAllowed(String key, int maxRequests, long windowSecs) {
        Instant now = Instant.now();

        windows.compute(key, (k, existing) -> {
            if (existing == null || now.isAfter(existing.windowStart().plusSeconds(windowSecs))) {
                return new Window(new AtomicInteger(1), now);
            }
            existing.count().incrementAndGet();
            return existing;
        });

        Window window = windows.get(key);
        return window != null && window.count().get() <= maxRequests;
    }

    @Override
    public void evictExpired(long windowSecs) {
        Instant cutoff = Instant.now().minusSeconds(windowSecs * 2);
        windows.entrySet().removeIf(e -> e.getValue().windowStart().isBefore(cutoff));
    }
}