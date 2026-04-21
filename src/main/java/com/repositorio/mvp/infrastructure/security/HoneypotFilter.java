package com.repositorio.mvp.infrastructure.security;

import java.io.IOException;
import java.util.Set;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.repositorio.mvp.domain.auth.service.security.RateLimitingService;
import com.repositorio.mvp.infrastructure.security.util.ClientIp;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Filtro Honeypot (Armadilha) para detectar e banir ferramentas de reconhecimento (Gobuster, scanners).
 * Monitora caminhos comuns usados em ataques (ex: /.env, /admin) e bane o IP do atacante
 * por 24 horas no primeiro toque, protegendo a infraestrutura contra ataques de enumeração.
 */
@Slf4j
@Component
@Order(0)
@RequiredArgsConstructor
public class HoneypotFilter extends OncePerRequestFilter {

    private final RateLimitingService rateLimitingService;

    private static final Set<String> HONEYPOT_PATHS = Set.of(
        "/.env",
        "/.git",
        "/admin",
        "/wp-admin",
        "/phpmyadmin",
        "/.htaccess",
        "/config.php",
        "/config.sh",
        "/backup.sql",
        "/database.sql",
        "/api/.env"
    );

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/h2-console") || path.startsWith("/swagger-ui") || path.startsWith("/v3/api-docs");
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        String path = request.getRequestURI();
        String ip = ClientIp.getClientIp(request);
        
        log.info("DEBUG SECURITY: IP extraído: '{}', Path: '{}'", ip, path);

        if (isLocalhost(ip)) {
            filterChain.doFilter(request, response);
            return;
        }

        if (isHoneypotPath(path)) {
            log.error("ALERTA DE SEGURANÇA: IP {} atingiu o Honeypot no caminho: {}. Banindo IP por 24h.", ip, path);
            rateLimitingService.banIp(ip);
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean isLocalhost(String ip) {
        return "127.0.0.1".equals(ip) || "0:0:0:0:0:0:0:1".equals(ip) || "::1".equals(ip);
    }

    private boolean isHoneypotPath(String path) {
        if (path == null) return false;
        String lowerPath = path.toLowerCase();
        return HONEYPOT_PATHS.stream().anyMatch(lowerPath::startsWith);
    }
}
