package com.repositorio.mvp.service.auth;

import org.springframework.stereotype.Service;

import com.repositorio.mvp.model.token.InvalidToken;
import com.repositorio.mvp.repository.token.InvalidTokenRepository;
import com.repositorio.mvp.service.token.TokenService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SessionService {

    private final InvalidTokenRepository invalidTokenRepository;
    private final TokenService tokenService;
    
    private static final String BEARER_PREFIX = "Bearer ";

    @Transactional
    public void logout(String token){
        String tokenJWT = token.replace(BEARER_PREFIX,"");
        InvalidToken invalidToken = new InvalidToken(tokenJWT, tokenService.getExpiration(tokenJWT));
        invalidTokenRepository.save(invalidToken);
    }
}