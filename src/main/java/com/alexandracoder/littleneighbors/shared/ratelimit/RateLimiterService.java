package com.alexandracoder.littleneighbors.shared.ratelimit;

public interface RateLimiterService {
    boolean isAllowed(String key, int maxRequests, long windowSecs);
    void evictExpired(long windowSecs);
}