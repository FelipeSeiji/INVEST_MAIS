package com.repositorio.mvp.mock.service.token;

import java.time.Instant;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.repositorio.mvp.domain.auth.token.service.TokenService;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
public class TokenServiceTest {

    @InjectMocks
    private TokenService tokenService;

    private final UUID mockUserId = UUID.randomUUID();
    private final String mockSecret = "secret-key";

    @BeforeEach
    void setUp() {
        tokenService = new TokenService();
        
        ReflectionTestUtils.setField(tokenService, "secret", mockSecret);
    }

    @Test
    public void generateToken_ReturnsValidJwtString() {
        String token = tokenService.generateToken(mockUserId);

        assertNotNull(token, "O token não pode ser nulo");
        assertFalse(token.isBlank(), "O token não pode estar em branco");
        
        String[] jwtParts = token.split("\\.");
        assertEquals(3, jwtParts.length, "O JWT gerado deve conter 3 partes (Header, Payload e Signature)");
    }

    @Test
    public void validateToken_WithValidToken_ReturnsSubjectId() {
        String validToken = tokenService.generateToken(mockUserId);

        String subject = tokenService.validateToken(validToken);

        assertEquals(mockUserId.toString(), subject);
    }

    @Test
    public void validateToken_WithInvalidOrTamperedToken_ThrowsException() {
        String tamperedToken = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJhdXRoLWFwaSIsInN1YiI6IjEyMyIsImlhdCI6MTcxMDAwMDAwMCwiZXhwIjoxNzEwMDAzNjAwfQ.assinatura_falsa_inventada";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            tokenService.validateToken(tamperedToken);
        });

        assertEquals("Token inválido", exception.getMessage());
    }

    @Test
    public void getExpiration_WithValidToken_ReturnsFutureInstant() {
        String validToken = tokenService.generateToken(mockUserId);

        Instant expiration = tokenService.getExpiration(validToken);

        assertNotNull(expiration);
        assertTrue(expiration.isAfter(Instant.now()), "A data de expiração do token deve ser no futuro");
    }
}