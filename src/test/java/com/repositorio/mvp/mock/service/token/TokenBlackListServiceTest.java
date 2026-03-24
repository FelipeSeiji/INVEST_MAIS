package com.repositorio.mvp.mock.service.token;

import java.time.Instant;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.repositorio.mvp.model.token.InvalidToken;
import com.repositorio.mvp.repository.token.InvalidTokenRepository;

import lombok.RequiredArgsConstructor;

/**
 * Serviço responsável por gerenciar a Blacklist de tokens JWT (usados em logouts).
 */
@Service
@RequiredArgsConstructor
public class TokenBlackListServiceTest {

    private final TokenServiceTest tokenService;
    private final InvalidTokenRepository invalidTokenRepository;

    /**
     * Verifica no banco de dados se um token específico foi invalidado (logout).
     * @param token Token JWT a ser verificado.
     * @return true se o token estiver na blacklist.
     */
    @Transactional(readOnly = true)
    public boolean isTokenInvalidated(String token) {
        return invalidTokenRepository.existsById(token);
    }

    /**
     * Adiciona manualmente um token à blacklist.
     * @param token Token JWT.
     * @param expiresAt Data/hora exata em que o token perde sua validade original.
     */
    @Transactional
    public void invalidateToken(String token, Instant expiresAt) {
        InvalidToken invalidToken = new InvalidToken(token, expiresAt);
        invalidTokenRepository.save(invalidToken);
    }

    /**
     * Método auxiliar de segurança (fail-safe) para checar se o token é válido antes da validação da blacklist.
     * @param token Token JWT.
     * @return true se o token for nulo, em branco, ou já estiver na blacklist.
     */
    public boolean isBlacklisted(String token) {
        if (tokenService == null || token.isBlank()) {
            return false;
        }
        return invalidTokenRepository.existsById(token);
    }

    /**
     * Rotina agendada (Cron Job) que executa automaticamente todos os dias às 03:00 da manhã.
     * Remove do banco de dados os tokens da blacklist que já expiraram de forma natural,
     * economizando espaço de armazenamento no banco de dados.
     */
    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void removeExpiredTokens() {
        invalidTokenRepository.deleteByExpiresAtBefore(Instant.now());
    }
}
