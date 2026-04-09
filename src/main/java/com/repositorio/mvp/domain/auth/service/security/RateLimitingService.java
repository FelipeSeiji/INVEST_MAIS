package com.repositorio.mvp.domain.auth.service.security;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Service
public class RateLimitingService {
    private final Cache<String, Bucket> cache = Caffeine.newBuilder()
        .expireAfterAccess(1, TimeUnit.HOURS)
        .maximumSize(10000)
        .build();

    public Bucket resolveBucket(String ip) {
        return cache.get(ip, this::newBucket);
    }

    private Bucket newBucket(String ip) {
        Bandwidth limit = Bandwidth.classic(50, 
            Refill.greedy(
                50, 
                Duration.ofMinutes(1)
            )
        );
        return Bucket.builder()
            .addLimit(limit)
            .build();
    }
}