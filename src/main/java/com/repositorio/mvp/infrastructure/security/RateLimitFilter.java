package com.repositorio.mvp.infrastructure.security;

import com.repositorio.mvp.common.constants.LogMessageConstants;
import com.repositorio.mvp.common.constants.MessageConstants;
import com.repositorio.mvp.infrastructure.security.util.ClientIp;
import com.repositorio.mvp.domain.auth.service.security.RateLimitingService;
import io.github.bucket4j.Bucket;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.repositorio.mvp.infrastructure.exception.RateLimitExceededException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

/**
 * Filtro de controle de vazão (Rate Limiting) por IP.
 * Utiliza o algoritmo Token Bucket (via Bucket4j) para proteger a API contra
 * ataques de negação de serviço (DoS) e brute-force.
 * IPs que excedem drasticamente o limite ou tentam acessar honeypots podem ser banidos.
 */
@Slf4j
@Component
@Order(1)
@RequiredArgsConstructor
public class RateLimitFilter extends OncePerRequestFilter {
    private final RateLimitingService rateLimitingService;
    @Qualifier("handlerExceptionResolver")
    private final HandlerExceptionResolver handlerExceptionResolver;

    @Override
    protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
        String path = request.getRequestURI();
        return path.startsWith("/h2-console") || path.startsWith("/swagger-ui") || path.startsWith("/v3/api-docs");
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        String ip = ClientIp.getClientIp(request);
        
        // Desenvolvedores em localhost ignoram rate limiting e banimento
        if (isLocalhost(ip)) {
            filterChain.doFilter(request, response);
            return;
        }

        if (rateLimitingService.isBanned(ip)) {
            log.warn(LogMessageConstants.SECURITY.RATE_LIMIT_EXCEEDED_DDOS, ip + " (BANNED)");
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        Bucket bucket = rateLimitingService.resolveBucket(ip);

        if (!bucket.tryConsume(1)) {
            log.warn(LogMessageConstants.SECURITY.RATE_LIMIT_EXCEEDED_DDOS, ip);
            handlerExceptionResolver.resolveException(request, response, null, new RateLimitExceededException(MessageConstants.Auth.ERR_RATELIMIT_EXCEEDED));
            return;
        }

        filterChain.doFilter(request, response);
    }

    private boolean isLocalhost(String ip) {
        return "127.0.0.1".equals(ip) || "0:0:0:0:0:0:0:1".equals(ip) || "::1".equals(ip);
    }
}