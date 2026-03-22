package com.repositorio.mvp.service.token;

import java.time.Instant;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;

@Service
public class TokenService {
    @Value("${api.security.token.secret}")
    private String secret;

    private static final String ISSUER = "auth-api";
    private static final long EXPIRATION_MINUTES = 10;

    public String generateToken(UUID userId){
        Algorithm algorithm = Algorithm.HMAC256(secret);
        Instant now = Instant.now();
            
        return JWT.create()
            .withIssuer(ISSUER)
            .withSubject(userId.toString())
            .withIssuedAt(now)
            .withExpiresAt(now.plusSeconds(EXPIRATION_MINUTES * 60))
            .sign(algorithm);   
    }

    public String validateToken(String token){
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);

            return JWT.require(algorithm)
                .withIssuer(ISSUER)
                .build()
                .verify(token)
                .getSubject();

        } catch (JWTVerificationException exception){
            throw new IllegalArgumentException("Token inválido");
        }
    }

    public Instant getExpiration(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);

            return JWT.require(algorithm)
                .withIssuer(ISSUER)
                .build()
                .verify(token)
                .getExpiresAt()
                .toInstant();
    } catch (JWTVerificationException exception) {
        throw new IllegalArgumentException("Token inválido");
    }
}
}