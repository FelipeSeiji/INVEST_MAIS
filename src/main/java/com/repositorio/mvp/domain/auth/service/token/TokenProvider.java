package com.repositorio.mvp.domain.auth.service.token;

import java.time.Instant;
import java.util.UUID;

public interface TokenProvider {
    String generateToken(UUID userId);
    String validateToken(String token);
    Instant getExpiration(String token);
}
