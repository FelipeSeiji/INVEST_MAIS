package com.repositorio.mvp.infrastructure.security;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.repositorio.mvp.common.constants.LogMessageConstants;
import com.repositorio.mvp.common.constants.MessageConstants;
import com.repositorio.mvp.domain.auth.service.token.TokenBlackListService;
import com.repositorio.mvp.domain.auth.service.token.TokenService;
import com.repositorio.mvp.domain.user.service.interfaces.UserQueryService;
import com.repositorio.mvp.infrastructure.security.util.ClientIp;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Filtro de Segurança executado em todas as requisições HTTP da API.
 * Responsável por extrair o token JWT, validá-lo contra expiração e blacklist,
 * e injetar a identidade do usuário no contexto do Spring Security.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SecurityFilter extends OncePerRequestFilter {

    private final TokenService tokenService;
    private final UserQueryService userService;
    private final TokenBlackListService invalidatedTokenService;

    /**
     * Método principal do filtro. Intercepta a requisição para verificar a presença
     * e a validade de um Token JWT antes de permitir que ela alcance os
     * Controllers.
     * 
     * @param request     Requisição HTTP de entrada.
     * @param response    Resposta HTTP de saída.
     * @param filterChain Cadeia de filtros de segurança do Spring.
     */
    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/h2-console") || path.startsWith("/swagger-ui") || path.startsWith("/v3/api-docs");
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {
        String token = this.recoverToken(request);

        if (token != null && !invalidatedTokenService.isBlacklisted(token)) {
            authenticateClient(token, request);
        }
        filterChain.doFilter(request, response);
    }

    /**
     * Descriptografa o token, carrega os dados do usuário e estabelece a sessão
     * segura atual.
     * Em caso de falha (token corrompido, expirado ou usuário deletado), registra o
     * incidente nos logs.
     * 
     * @param token   String contendo o JWT (sem o prefixo Bearer).
     * @param request Requisição HTTP usada para capturar o IP do cliente em caso de fraude.
     */
    private void authenticateClient(@NonNull String token, @NonNull HttpServletRequest request) {
        try {
            String subjectId = tokenService.validateToken(token);

            UserDetails userDetails = userService.loadUserDetailsById(subjectId);

            var authentication = new UsernamePasswordAuthenticationToken(
                    userDetails,
                    null,
                    userDetails.getAuthorities());

            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (Exception e) {
            String ip = ClientIp.getClientIp(request);
            log.warn(LogMessageConstants.SECURITY.JWT_VALIDATION_FAILED,
                    ip, request.getRequestURI(), e.getMessage());
            SecurityContextHolder.clearContext();
        }
    }

    /**
     * Extrai a string do JWT do cabeçalho "Authorization" no formato padrão
     * (Bearer).
     * 
     * @param request Requisição HTTP contendo os cabeçalhos.
     * @return O token limpo (se presente) ou null caso o formato seja incorreto.
     */
    private String recoverToken(@NonNull HttpServletRequest request) {
        var authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith(MessageConstants.Auth.BEARER_PREFIX)) {
            return null;
        }
        return authHeader.substring(MessageConstants.Auth.BEARER_PREFIX.length());
    }
}