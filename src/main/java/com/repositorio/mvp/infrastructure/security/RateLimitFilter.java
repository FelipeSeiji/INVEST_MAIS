package com.repositorio.mvp.infrastructure.security;

import com.repositorio.mvp.infrastructure.security.util.ClientIp;
import com.repositorio.mvp.domain.auth.service.security.RateLimitingService;
import io.github.bucket4j.Bucket;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.io.IOException;

@Slf4j
@Component
public class RateLimitFilter extends OncePerRequestFilter {
    private final RateLimitingService rateLimitingService;
    private final HandlerExceptionResolver handlerExceptionResolver;

    public RateLimitFilter(
            @NonNull RateLimitingService rateLimitingService, 
            @Qualifier("handlerExceptionResolver") @NonNull HandlerExceptionResolver handlerExceptionResolver) {
        this.rateLimitingService = rateLimitingService;
        this.handlerExceptionResolver = handlerExceptionResolver;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        String ip = ClientIp.getClientIp(request);
        Bucket bucket = rateLimitingService.resolveBucket(ip);

        if (bucket.tryConsume(1)) {
            filterChain.doFilter(request, response);
        } else {
            log.warn("BLOQUEIO DDoS (Rate Limit Excedido): IP {} disparou requisições demais.", ip);

            handlerExceptionResolver.resolveException(request, response, null, new RuntimeException("Muitas requisições. Por favor, aguarde alguns instantes."));
        }
    }
}