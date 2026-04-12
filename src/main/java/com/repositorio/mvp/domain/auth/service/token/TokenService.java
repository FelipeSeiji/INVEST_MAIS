package com.repositorio.mvp.domain.auth.service.token;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import lombok.NonNull;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;

/**
 * Serviço core de criptografia e gerenciamento de JSON Web Tokens (JWT).
 */
@Service
public class TokenService implements TokenProvider {
    private static final String MESSAGE_ERR_INVALID_TOKEN = "Token inválido";

    @Value("${api.security.token.secret}")
    private String secret;

    private static final String ISSUER = "auth-api";
    private static final long EXPIRATION_MINUTES = 60;

    /**
     * Gera um novo token JWT assinado usando o algoritmo HMAC256.
     * Define o emissor (Issuer), o assunto (Subject) como o ID do usuário,
     * e os instantes de emissão e expiração.
     * 
     * @param userId Identificador único do usuário a ser embutido no payload.
     * @return Token JWT serializado em Base64Url pronto para ser enviado ao cliente.
     */
    @Override
    public String generateToken(@NonNull UUID userId) {
        Algorithm algorithm = Algorithm.HMAC256(secret);
        Instant now = Instant.now();

        return JWT.create()
            .withIssuer(ISSUER)
            .withSubject(userId.toString())
            .withIssuedAt(now)
            .withExpiresAt(now.plus(
                Duration.ofMinutes(EXPIRATION_MINUTES)
                )
            )
            .sign(algorithm);
    }

    /**
     * Valida a integridade, o emissor e a validade temporal do token JWT.
     * 
     * @param token Token JWT recebido nas requisições protegidas.
     * @return O identificador do usuário (Subject) extraído do token validado.
     * @throws IllegalArgumentException Se a assinatura for inválida, o emissor estiver incorreto ou o token estiver expirado.
     */
    @Override
    public String validateToken(@NonNull String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);

            return JWT.require(algorithm)
                .withIssuer(ISSUER)
                .build()
                .verify(token)
                .getSubject();

        } catch (JWTVerificationException exception) {
            throw new IllegalArgumentException(MESSAGE_ERR_INVALID_TOKEN);
        }
    }

    /**
     * Recupera o instante exato de expiração do token sem a necessidade de 
     * processar manualmente o payload Base64. Utilizada para gerenciamento de Blacklist.
     * 
     * @param token Token JWT a ser analisado.
     * @return Instant contendo a data e hora de expiração.
     * @throws IllegalArgumentException Se o token estiver malformado ou sua assinatura for inválida.
     */
    @Override
    public Instant getExpiration(@NonNull String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);

            return JWT.require(algorithm)
                .withIssuer(ISSUER)
                .build()
                .verify(token)
                .getExpiresAt()
                .toInstant();
        } catch (JWTVerificationException exception) {
            throw new IllegalArgumentException(MESSAGE_ERR_INVALID_TOKEN);
        }
    }
}