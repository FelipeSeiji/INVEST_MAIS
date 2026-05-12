package com.repositorio.mvp.domain.auth.token.service;

import java.time.Instant;
import java.util.UUID;

/**
 * Interface que define as operações fundamentais para provedores de tokens de segurança.
 * Isolam a lógica de geração e validação permitindo diferentes implementações (ex: JWT, Paseto).
 */
public interface TokenProviderService {
    /**
     * Gera um novo token de segurança para o usuário informado.
     * 
     * @param userId UUID do usuário que receberá o token.
     * @return String contendo o token gerado.
     */
    String generateToken(UUID userId);

    /**
     * Valida a integridade e validade de um token de segurança.
     * 
     * @param token String do token a ser validado.
     * @return O identificador do usuário (Subject) caso o token seja válido.
     */
    String validateToken(String token);

    /**
     * Extrai a data de expiração de um token de segurança.
     * 
     * @param token String do token.
     * @return Instant contendo o momento da expiração.
     */
    Instant getExpiration(String token);
}
