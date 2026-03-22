package com.repositorio.mvp.service.token;

import java.time.Instant;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.repositorio.mvp.model.token.InvalidToken;
import com.repositorio.mvp.repository.token.InvalidTokenRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TokenBlackListService {

    private final TokenService tokenService;
    private final InvalidTokenRepository invalidTokenRepository;

    @Transactional(readOnly = true)
    public boolean isTokenInvalidated(String token) {
        return invalidTokenRepository.existsById(token);
    }

    @Transactional
    public void invalidateToken(String token, Instant expiresAt) {
        InvalidToken invalidToken = new InvalidToken(token, expiresAt);
        invalidTokenRepository.save(invalidToken);
    }

    public boolean isBlacklisted(String token) {
        if (tokenService == null || token.isBlank()) {
            return false;
        }
        return invalidTokenRepository.existsById(token);
    }

    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void removeExpiredTokens() {
        invalidTokenRepository.deleteByExpiresAtBefore(Instant.now());
    }
}
