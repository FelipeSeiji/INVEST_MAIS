package com.repositorio.mvp.domain.auth.service.interfaces;

import java.time.Instant;
import java.util.UUID;

/**
 * Interface que define as operações fundamentais para provedores de tokens de segurança.
 * Isolam a lógica de geração e validação permitindo diferentes implementações (ex: JWT, Paseto).
 */
public interface TokenProvider {
    String generateToken(UUID userId);

    String validateToken(String token);

    Instant getExpiration(String token);
}
