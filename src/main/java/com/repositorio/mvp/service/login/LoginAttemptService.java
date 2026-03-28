package com.repositorio.mvp.service.login;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

/**
 * Serviço responsável por prevenir ataques de força bruta (Brute Force) e preenchimento de credenciais (Credential Stuffing).
 * Controla o número de tentativas falhas de login por IP ou E-mail, aplicando bloqueios temporários.
 */
@Service
public class LoginAttemptService {
    private static final int MAX_ATTEMPTS = 5;
    private static final long BLOCK_DURATION_SECONDS = 900; 

    private final Map<String, Integer> attemptsCache = new ConcurrentHashMap<>();
    private final Map<String, Instant> blockedIps = new ConcurrentHashMap<>();

    /**
     * Registra um login bem-sucedido e limpa o histórico de falhas daquela chave (IP ou E-mail).
     * @param key Chave de identificação (IP do cliente ou E-mail do usuário).
     */
    public void loginSucceeded(String ip) {
        attemptsCache.remove(ip);
        blockedIps.remove(ip);
    }

    /**
     * Registra uma tentativa falha de login.
     * Se o limite de tentativas for atingido, a chave é bloqueada temporariamente.
     * @param key Chave de identificação (IP do cliente ou E-mail do usuário).
     */
    
    public void loginFailed(String ip) {
        int attempts = attemptsCache.getOrDefault(ip, 0) + 1;
        attemptsCache.put(ip, attempts);

        if (attempts >= MAX_ATTEMPTS) {
            blockedIps.put(ip, Instant.now().plusSeconds(BLOCK_DURATION_SECONDS));
        }
    }

    /**
     * Verifica se uma determinada chave (IP ou E-mail) está atualmente bloqueada.
     * Caso o tempo de bloqueio já tenha expirado, a chave é liberada automaticamente.
     * @param key Chave de identificação (IP do cliente ou E-mail do usuário).
     * @return true se estiver bloqueado, false caso contrário.
     */
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

    /**
     * Consulta a quantidade atual de tentativas falhas registradas para uma chave.
     * @param key Chave de identificação (IP do cliente ou E-mail do usuário).
     * @return Número inteiro representando a quantidade de falhas.
     */
    public int getAttempts(String ip) {
        return attemptsCache.getOrDefault(ip, 0);
    }
}
