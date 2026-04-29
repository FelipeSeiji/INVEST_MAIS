package com.repositorio.mvp.domain.auth.service.interfaces;

import java.time.Instant;
import java.util.UUID;

/**
 * Interface que define as operações fundamentais para provedores de tokens de segurança.
 * Isolam a lógica de geração e validação permitindo diferentes implementações (ex: JWT, Paseto).
 */
public interface TokenProvider {
    /**
     * Gera um token de acesso para um usuário específico.
     * 
     * @param userId UUID do usuário autenticado.
     * @return String representativa do token.
     */
    String generateToken(UUID userId);

    /**
     * Valida a integridade e autenticidade de um token.
     * 
     * @param token String do token a ser validado.
     * @return O identificador (Subject) embutido no token se for válido.
     */
    String validateToken(String token);

    /**
     * Recupera o instante exato de expiração do token.
     * 
     * @param token String do token.
     * @return Instant contendo a data e hora de expiração.
     */
    Instant getExpiration(String token);
}
