package com.repositorio.mvp.domain.auth.service.login;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.stereotype.Service;
import java.util.concurrent.TimeUnit;

@Service
public class LoginAttemptService {
    private static final int MAX_ATTEMPTS = 5;

    // Proteção de Memória: Guarda no máximo 10.000 IPs e expira automaticamente após 15 minutos.
    private final Cache<String, Integer> attemptsCache = Caffeine.newBuilder()
            .expireAfterWrite(15, TimeUnit.MINUTES)
            .maximumSize(10000)
            .build();

    public void loginSucceeded(String key) {
        attemptsCache.invalidate(key);
    }

    public void loginFailed(String key) {
        // Se a chave não existir, inicializa com 0 e soma 1.
        int attempts = attemptsCache.get(key, k -> 0) + 1;
        attemptsCache.put(key, attempts);
    }

    public boolean isBlocked(String key) {
        return attemptsCache.get(key, k -> 0) >= MAX_ATTEMPTS;
    }

    public int getAttempts(String key) {
        Integer attempts = attemptsCache.getIfPresent(key);
        return attempts != null ? attempts : 0;
    }
}