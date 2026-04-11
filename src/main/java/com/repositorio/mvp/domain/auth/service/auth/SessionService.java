package com.repositorio.mvp.domain.auth.service.auth;

import org.springframework.stereotype.Service;

import com.repositorio.mvp.domain.auth.model.InvalidToken;
import com.repositorio.mvp.domain.auth.repository.InvalidTokenRepository;
import com.repositorio.mvp.domain.auth.service.token.TokenBlackListService;
import com.repositorio.mvp.domain.auth.service.token.TokenProvider;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

/**
 * Serviço de gerenciamento de sessões do usuário.
 */
@Service
@RequiredArgsConstructor
public class SessionService {

    private final TokenBlackListService tokenBlackListService;
    private final TokenProvider tokenProvider;
    
    private static final String BEARER_PREFIX = "Bearer ";

    /**
     * Efetua o logout do usuário invalidando o token JWT atual.
     * Como tokens JWT são stateless e não podem ser "deletados" do cliente pelo servidor, 
     * o token é adicionado a uma Blacklist (com base na sua data de expiração) para 
     * impedir que seja reutilizado em requisições futuras.
     * 
     * @param token String do token JWT (pode conter o prefixo "Bearer ").
     */
    @Transactional
    public void logout(String token){
        String tokenJWT = token.replace(BEARER_PREFIX,"");
        tokenBlackListService.invalidateToken(
            tokenJWT,
            tokenProvider.getExpiration(tokenJWT)
        );
    }
}