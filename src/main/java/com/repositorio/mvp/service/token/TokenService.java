package com.repositorio.mvp.service.token;

import java.time.Instant;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;

/**
 * Serviço core de criptografia e gerenciamento de JSON Web Tokens (JWT).
 */
@Service
public class TokenService {
    @Value("${api.security.token.secret}")
    private String secret;

    private static final String ISSUER = "auth-api";
    private static final long EXPIRATION_MINUTES = 10;

    /**
     * Gera um novo token JWT assinado usando o algoritmo HMAC256.
     * @param userId Identificador único do usuário a ser embutido no payload (Subject).
     * @return Token JWT serializado em Base64Url.
     */
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

    /**
     * Valida a integridade, o emissor e a data de expiração do token.
     * @param token Token JWT recebido nas requisições protegidas.
     * @return O ID do usuário (Subject) em formato String caso o token seja válido.
     * @throws IllegalArgumentException Se a assinatura for inválida, o token estiver adulterado ou expirado.
     */
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

    /**
     * Descriptografa o token apenas para extrair o momento exato de sua expiração (útil para blacklists).
     * @param token Token JWT a ser analisado.
     * @return Data e hora da expiração (Instant).
     * @throws IllegalArgumentException Se o token estiver malformado ou inválido.
     */
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