package com.repositorio.mvp.config;

import com.repositorio.mvp.controller.auth.util.ClientIp;
import com.repositorio.mvp.service.security.RateLimitingService;
import io.github.bucket4j.Bucket;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Filtro global que intercepta todas as requisições para checar o limite de taxa (Rate Limit).
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RateLimitFilter extends OncePerRequestFilter {

    private final RateLimitingService rateLimitingService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String ip = ClientIp.getClientIp(request);
        Bucket bucket = rateLimitingService.resolveBucket(ip);

        // Tenta consumir 1 ficha do balde
        if (bucket.tryConsume(1)) {
            // Ficha consumida com sucesso! Segue o fluxo normal.
            filterChain.doFilter(request, response);
        } else {
            // Sem fichas! Bloqueia o tráfego e retorna 429.
            log.warn("BLOQUEIO DDoS (Rate Limit Excedido): IP {} disparou requisições demais.", ip);
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"erro\": \"Muitas requisições. Por favor, aguarde alguns instantes e tente novamente.\"}");
        }
    }
}