package com.repositorio.mvp.service.auth;

import org.springframework.stereotype.Service;

import com.repositorio.mvp.model.token.InvalidToken;
import com.repositorio.mvp.repository.token.InvalidTokenRepository;
import com.repositorio.mvp.service.token.TokenService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

/**
 * Serviço de gerenciamento de sessões do usuário.
 */
@Service
@RequiredArgsConstructor
public class SessionService {

    private final InvalidTokenRepository invalidTokenRepository;
    private final TokenService tokenService;
    
    private static final String BEARER_PREFIX = "Bearer ";

    /**
     * Efetua o logout de um usuário adicionando o seu token JWT atual a uma Blacklist.
     * Como JWTs são stateless e não podem ser apagados remotamente, essa é a técnica segura de invalidação.
     * @param token String do token JWT bruto (podendo conter o prefixo "Bearer ").
     */
    @Transactional
    public void logout(String token){
        String tokenJWT = token.replace(BEARER_PREFIX,"");
        InvalidToken invalidToken = new InvalidToken(tokenJWT, tokenService.getExpiration(tokenJWT));
        invalidTokenRepository.save(invalidToken);
    }
}