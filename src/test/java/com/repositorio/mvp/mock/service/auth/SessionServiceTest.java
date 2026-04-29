package com.repositorio.mvp.mock.service.auth;

import java.time.Instant;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.repositorio.mvp.domain.auth.service.auth.LogoutService;
import com.repositorio.mvp.domain.auth.service.interfaces.TokenProvider;
import com.repositorio.mvp.domain.auth.service.token.TokenBlackListService;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SessionServiceTest {

    @InjectMocks
    private LogoutService sessionService;

    @Mock
    private TokenBlackListService tokenBlackListService;
    
    @Mock
    private TokenProvider tokenProvider;

    @Test
    public void logout_WithBearerPrefix_RemovesPrefixAndSavesToBlacklist() {
        String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.token_jwt_valido";
        String tokenWithBearer = "Bearer " + token;
        Instant mockExpirationDate = Instant.now().plusSeconds(3600);

        when(tokenProvider.getExpiration(token)).thenReturn(mockExpirationDate);

        sessionService.logout(tokenWithBearer);

        verify(tokenBlackListService).invalidateToken(token, mockExpirationDate);
    }

    @Test
    public void logout_WithoutBearerPrefix_SavesToBlacklist() {
        String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.another_valid_jwt";
        Instant mockExpirationDate = Instant.now().plusSeconds(1800);

        when(tokenProvider.getExpiration(token)).thenReturn(mockExpirationDate);

        sessionService.logout(token);

        verify(tokenBlackListService).invalidateToken(token, mockExpirationDate);
    }
}