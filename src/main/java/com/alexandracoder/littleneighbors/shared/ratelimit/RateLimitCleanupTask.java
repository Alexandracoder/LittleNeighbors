package com.alexandracoder.littleneighbors.shared.ratelimit;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RateLimitCleanupTask {

    private final RateLimiterService rateLimiterService;

    @Scheduled(fixedDelay = 600_000)
    public void cleanup() {
        rateLimiterService.evictExpired(3600);
    }
}