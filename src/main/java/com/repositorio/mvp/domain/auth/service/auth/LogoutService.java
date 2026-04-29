package com.repositorio.mvp.domain.auth.service.auth;

import jakarta.transaction.Transactional;

import org.springframework.stereotype.Service;

import com.repositorio.mvp.common.result.ServiceResult;
import com.repositorio.mvp.domain.auth.service.interfaces.TokenProviderService;
import com.repositorio.mvp.domain.auth.service.token.TokenBlackListService;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/**
 * Serviço de gerenciamento de sessões do usuário.
 */
@Service
@RequiredArgsConstructor
public class LogoutService {

    private final TokenBlackListService tokenBlackListService;
    private final TokenProviderService tokenProvider;
    
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
    public ServiceResult<Void> logout(@NonNull String token) {
        String tokenJWT = token.replace(BEARER_PREFIX, "");
        tokenBlackListService.invalidateToken(
            tokenJWT,
            tokenProvider.getExpiration(tokenJWT)
        );
        return ServiceResult.success(null);
    }
}