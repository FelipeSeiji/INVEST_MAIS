package com.repositorio.mvp.domain.auth.service.token;

import java.time.Instant;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.repositorio.mvp.domain.auth.model.InvalidToken;
import com.repositorio.mvp.domain.auth.repository.InvalidTokenRepository;
import org.apache.commons.codec.digest.DigestUtils;

import lombok.RequiredArgsConstructor;

/**
 * Serviço responsável por gerenciar a Blacklist de tokens JWT (usados em logouts).
 */
@Service
@RequiredArgsConstructor
public class TokenBlackListService {

    private final InvalidTokenRepository invalidTokenRepository;

    /**
     * Verifica no banco de dados se um token específico foi invalidado anteriormente (via logout).
     * Utiliza o hash SHA-256 do token para a busca por motivos de segurança e performance.
     * 
     * @param token Token JWT a ser verificado.
     * @return true se o token constar na blacklist, false caso contrário.
     */
    @Transactional(readOnly = true)
    public boolean isTokenInvalidated(String token) {
        return invalidTokenRepository.existsById(DigestUtils.sha256Hex(token));
    }

    /**
     * Adiciona um token à blacklist de forma permanente até sua expiração natural.
     * 
     * @param token String do token JWT bruto.
     * @param expiresAt Instante (Instant) da expiração original do token.
     */
    @Transactional
    public void invalidateToken(String token, Instant expiresAt) {
        InvalidToken invalidToken = new InvalidToken(DigestUtils.sha256Hex(token), expiresAt);
        invalidTokenRepository.save(invalidToken);
    }

    /**
     * Método auxiliar para checagem rápida de blacklist.
     * 
     * @param token String do token JWT.
     * @return true se o token for nulo, em branco, ou constar na blacklist.
     */
    public boolean isBlacklisted(String token) {
        if (token == null || token.isBlank()) {
            return false;
        }
        return invalidTokenRepository.existsById(DigestUtils.sha256Hex(token));
    }

    /**
     * Rotina agendada (Cron Job) que executa automaticamente todos os dias às 03:00 da manhã.
     * Remove registros de tokens cujas datas de expiração já passaram, mantendo a 
     * limpeza e performance da tabela de invalidação.
     */
    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void removeExpiredTokens() {
        invalidTokenRepository.deleteByExpiresAtBefore(Instant.now());
    }
}
