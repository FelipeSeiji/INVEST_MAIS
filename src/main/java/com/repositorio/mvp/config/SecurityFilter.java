package com.repositorio.mvp.config;

import java.io.IOException;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.repositorio.mvp.controller.auth.util.ClientIp;
import com.repositorio.mvp.service.interfaces.UserCommandService;
import com.repositorio.mvp.service.token.TokenBlackListService;
import com.repositorio.mvp.service.token.TokenService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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
    private final UserCommandService userService;
    private final TokenBlackListService invalidatedTokenService;

    /**
     * Método principal do filtro. Intercepta a requisição para verificar a presença 
     * e a validade de um Token JWT antes de permitir que ela alcance os Controllers.
     * @param request Requisição HTTP de entrada.
     * @param response Resposta HTTP de saída.
     * @param filterChain Cadeia de filtros de segurança do Spring.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) 
        throws ServletException, IOException {       
        String token = this.recoverToken(request);

        if (token != null && !invalidatedTokenService.isBlacklisted(token)) {
            authenticateClient(token, request);
        }
        filterChain.doFilter(request, response);
    }

    /**
     * Descriptografa o token, carrega os dados do usuário e estabelece a sessão segura atual.
     * Em caso de falha (token corrompido, expirado ou usuário deletado), registra o incidente nos logs.
     * @param token String contendo o JWT (sem o prefixo Bearer).
     * @param request Requisição HTTP usada para capturar o IP do cliente em caso de fraude.
     */
    private void authenticateClient(String token, HttpServletRequest request) {
        try {
            String subjectId = tokenService.validateToken(token);
            
            UserDetails userDetails = userService.loadUserDetailsById(subjectId);
            
            var authentication = new UsernamePasswordAuthenticationToken(
                userDetails,
                null,
                userDetails.getAuthorities()
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (Exception e) {
            String ip = ClientIp.getClientIp(request);
            log.warn("ACESSO NEGADO: Falha na validação do JWT. IP: {} | URI: {} | Motivo: {}", 
            ip, request.getRequestURI(), e.getMessage());
            SecurityContextHolder.clearContext();
        }
    }

    /**
     * Extrai a string do JWT do cabeçalho "Authorization" no formato padrão (Bearer).
     * @param request Requisição HTTP contendo os cabeçalhos.
     * @return O token limpo (se presente) ou null caso o formato seja incorreto.
     */
    private String recoverToken(HttpServletRequest request) {
        var authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }
        return authHeader.substring(7);
    }
}