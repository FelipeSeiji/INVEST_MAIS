package com.repositorio.mvp.service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;

@Service
public class TokenService {
    @Value("${api.security.token.secret}")
    private String secret;
    //gera token JWT
    public String generateToken(UUID userId){
        try{
            Algorithm algorithm = Algorithm.HMAC256(secret);
            String token = JWT.create()
                .withIssuer("auth-api")
                .withSubject(userId.toString())
                .withExpiresAt(genExpirationDate())
                .sign(algorithm);
            return token;    
        } catch (JWTCreationException exception){
            throw new RuntimeException("Erro ao gerar token JWT", exception);
            
        }
        
    }
    //valida o token JWT e agora retorna o ID do usuário 
    public Optional<UUID> validateToken(String token){
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            String subject = JWT.require(algorithm)
                    .withIssuer("auth-api")
                    .build()
                    .verify(token)
                    .getSubject();
            return Optional.of(UUID.fromString(subject));     
        } catch (JWTVerificationException exception){
            return Optional.empty();
        }
    }

    private Instant genExpirationDate(){
        return LocalDateTime.now().plusDays(2).toInstant(ZoneOffset.of("-03:00"));//Sessãos com tempo de expiração
    }
    //desloga um adicionando o token a uma lista de tokens inválidos
    public Instant getExpiration(String token) {
    try {
        Algorithm algorithm = Algorithm.HMAC256(secret);

        return JWT.require(algorithm)
                .withIssuer("auth-api")
                .build()
                .verify(token)
                .getExpiresAt()
                .toInstant();

    } catch (JWTVerificationException exception) {
        throw new RuntimeException("Token inválido", exception);
    }
}
}
