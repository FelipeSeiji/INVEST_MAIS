package com.repositorio.mvp.service.token;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;

@Service
public class TokenServiceImpl {
    private String secret;

    public TokenServiceImpl(@Value("${api.security.token.secret}") String secret) {
        this.secret = secret;
    }

    @Override
    public String generateToken(UUID userId) {
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

    @Override
    public String validateToken(String token)
    {
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            JWT.require(algorithm)
                .withIssuer("auth-api")
                .build()
                .verify(token);
            return JWT.decode(token).getSubject();
        } catch (JWTVerificationException exception) {
            throw new RuntimeException("Token JWT inválido", exception);
        }
    }


    private Date genExpirationDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR, 2);
        return calendar.getTime();
    }
}
