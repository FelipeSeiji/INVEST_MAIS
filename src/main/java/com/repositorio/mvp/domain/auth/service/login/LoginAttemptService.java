package com.repositorio.mvp.domain.auth.service.login;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.stereotype.Service;
import java.util.concurrent.TimeUnit;

/**
 * Serviço responsável por monitorar e mitigar ataques de força bruta.
 * Utiliza um cache em memória (Caffeine) para rastrear falhas de autenticação 
 * por chave (IP ou e-mail) com expiração automática.
 */
@Service
public class LoginAttemptService {
    private static final int MAX_ATTEMPTS = 5;
    private final Cache<String, Integer> attemptsCache = Caffeine.newBuilder()
            .expireAfterWrite(15, TimeUnit.MINUTES)
            .maximumSize(10000)
            .build();

    /**
     * Registra que uma tentativa de login foi bem-sucedida, limpando o histórico de falhas da chave.
     * 
     * @param key Identificador da tentativa (ex: IP ou e-mail).
     */
    public void loginSucceeded(String key) {
        attemptsCache.invalidate(key);
    }

    /**
     * Incrementa o contador de falhas para uma determinada chave.
     * 
     * @param key Identificador da tentativa (ex: IP ou e-mail).
     */
    public void loginFailed(String key) {
        int attempts = attemptsCache.get(key, k -> 0) + 1;
        attemptsCache.put(key, attempts);
    }

    /**
     * Verifica se uma determinada chave atingiu o limite máximo de tentativas falhas.
     * 
     * @param key Identificador da tentativa (ex: IP ou e-mail).
     * @return true se a chave estiver bloqueada (>= 5 tentativas), false caso contrário.
     */
    public boolean isBlocked(String key) {
        return attemptsCache.get(key, k -> 0) >= MAX_ATTEMPTS;
    }

    /**
     * Recupera o número atual de tentativas falhas registradas para uma chave.
     * 
     * @param key Identificador da tentativa.
     * @return O contador de falhas atual.
     */
    public int getAttempts(String key) {
        Integer attempts = attemptsCache.getIfPresent(key);
        return attempts != null ? attempts : 0;
    }
}