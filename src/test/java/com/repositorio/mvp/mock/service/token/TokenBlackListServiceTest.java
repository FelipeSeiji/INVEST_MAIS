package com.repositorio.mvp.mock.service.token;

import java.time.Instant;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.repositorio.mvp.common.security.CryptoService;
import com.repositorio.mvp.domain.auth.model.InvalidToken;
import com.repositorio.mvp.domain.auth.repository.InvalidTokenRepository;
import com.repositorio.mvp.domain.auth.service.token.TokenBlackListService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class TokenBlackListServiceTest {

    @InjectMocks
    private TokenBlackListService tokenBlackListService;

    @Mock
    private InvalidTokenRepository invalidTokenRepository;

    @Mock
    private CryptoService cryptoService;

    @Captor
    private ArgumentCaptor<InvalidToken> invalidTokenCaptor;

    @Test
    public void invalidateToken_SavesTokenToBlacklist() {
        String token = "secret_token";
        Instant expiredAt = Instant.now().plusSeconds(3600);
        String hashedToken = DigestUtils.sha256Hex(token);

        when(cryptoService.generateSha256Hash(token)).thenReturn(hashedToken);

        tokenBlackListService.invalidateToken(token, expiredAt);

        verify(invalidTokenRepository).save(invalidTokenCaptor.capture());
        InvalidToken savedToken = invalidTokenCaptor.getValue();

        assertEquals(hashedToken, savedToken.getToken());
        assertEquals(expiredAt, savedToken.getExpiresAt());
    }


    @Test
    public void isBlacklisted_WithNullOrBlankToken_ReturnsFalse() {
        assertFalse(tokenBlackListService.isBlacklisted(null));
        assertFalse(tokenBlackListService.isBlacklisted(""));
        assertFalse(tokenBlackListService.isBlacklisted(" "));

        verify(invalidTokenRepository, never()).existsById(anyString());
    }

    @Test
    public void isBlacklisted_WithValidToken_QueriesRepository() {
        String token = "valid_token";
        String hashedToken = DigestUtils.sha256Hex(token);
        when(cryptoService.generateSha256Hash(token)).thenReturn(hashedToken);
        when(invalidTokenRepository.existsById(hashedToken)).thenReturn(true);

        boolean isBlacklisted = tokenBlackListService.isBlacklisted(token);

        assertTrue(isBlacklisted);

        verify(invalidTokenRepository).existsById(hashedToken);
    }

    @Test
    public void isBlacklisted_WithInvalidToken_QueriesRepository() {
        String token = "invalid_token";
        String hashedToken = DigestUtils.sha256Hex(token);
        when(cryptoService.generateSha256Hash(token)).thenReturn(hashedToken);
        when(invalidTokenRepository.existsById(hashedToken)).thenReturn(false);

        boolean isBlacklisted = tokenBlackListService.isBlacklisted(token);

        assertFalse(isBlacklisted);

        verify(invalidTokenRepository).existsById(hashedToken);
    }

    @Test
    public void removeExpiredTokens_CallsRepositoryToDeleteOldTokens() {
        tokenBlackListService.removeExpiredTokens();

        verify(invalidTokenRepository).deleteByExpiresAtBefore(any(Instant.class));
    }
}
