package com.repositorio.mvp.mock.service.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Instant;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import com.repositorio.mvp.model.token.InvalidToken;
import com.repositorio.mvp.repository.token.InvalidTokenRepository;
import com.repositorio.mvp.service.auth.SessionService;
import com.repositorio.mvp.service.token.TokenService;

import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class SessionServiceTest {

    @InjectMocks
    private SessionService sessionService;

    @Mock
    private InvalidTokenRepository invalidTokenRepository;
    
    @Mock
    private TokenService tokenService;

    @Captor
    private ArgumentCaptor<InvalidToken> invalidTokenCaptor;

    @Test
    public void logout_WithBearerPrefix_RemovesPrefixAndSavesToBlacklist() {
        String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.token_jwt_valido";
        String tokenWithBearer = "Bearer " + token;
        Instant mockExpirationDate = Instant.now().plusSeconds(3600);

        when(tokenService.getExpiration(token)).thenReturn(mockExpirationDate);

        sessionService.logout(tokenWithBearer);

        verify(invalidTokenRepository).save(invalidTokenCaptor.capture());
        InvalidToken savedToken = invalidTokenCaptor.getValue();

        assertEquals(token, savedToken.getToken());
        assertEquals(mockExpirationDate, savedToken.getExpiresAt());
    }

    @Test
    public void logout_WithoutBearerPrefix_SavesToBlacklist() {
        String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.another_valid_jwt";
        Instant mockExpirationDate = Instant.now().plusSeconds(1800);

        when(tokenService.getExpiration(token)).thenReturn(mockExpirationDate);

        sessionService.logout(token);

        verify(invalidTokenRepository).save(invalidTokenCaptor.capture());
        InvalidToken savedToken = invalidTokenCaptor.getValue();

        assertEquals(token, savedToken.getToken());
        assertEquals(mockExpirationDate, savedToken.getExpiresAt());
    }
}