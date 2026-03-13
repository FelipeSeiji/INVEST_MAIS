package com.repositorio.mvp.service;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

@Service
public class LoginAttemptService {

    private static final int MAX_ATTEMPTS = 10;
    private static final long BLOCK_DURATION_SECONDS = 900; // 15 min

    private final Map<String, Integer> attemptsCache = new ConcurrentHashMap<>();
    private final Map<String, Instant> blockedIps = new ConcurrentHashMap<>();

    public void loginSucceeded(String ip) {
        attemptsCache.remove(ip);
        blockedIps.remove(ip);
    }

    public void loginFailed(String ip) {
        int attempts = attemptsCache.getOrDefault(ip, 0) + 1;
        attemptsCache.put(ip, attempts);

        if (attempts >= MAX_ATTEMPTS) {
            blockedIps.put(ip, Instant.now().plusSeconds(BLOCK_DURATION_SECONDS));
        }
    }

    public boolean isBlocked(String ip) {
        Instant blockedUntil = blockedIps.get(ip);

        if (blockedUntil == null) {
            return false;
        }

        if (blockedUntil.isBefore(Instant.now())) {
            blockedIps.remove(ip);
            attemptsCache.remove(ip);
            return false;
        }

        return true;
    }

    public int getAttempts(String ip) {
        return attemptsCache.getOrDefault(ip, 0);
    }
}