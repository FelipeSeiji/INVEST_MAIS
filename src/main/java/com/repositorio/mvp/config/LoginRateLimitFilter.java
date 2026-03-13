package com.repositorio.mvp.config;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class LoginRateLimitFilter extends OncePerRequestFilter{
    
    private final Map<String, Bucket> cache = new ConcurrentHashMap<>();

    private Bucket createNewBucket() {
        Bandwidth limit = Bandwidth.simple(5, Duration.ofMinutes(1));//5 por minuto
        return Bucket.builder()
                .addLimit(limit)
                .build();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
                                    throws ServletException, IOException {

        if (request.getRequestURI().equals("/api/auth/login")
                && request.getMethod().equals("POST")) {

            String ip = request.getRemoteAddr();

            Bucket bucket = cache.computeIfAbsent(ip, k -> createNewBucket());

            if (!bucket.tryConsume(1)) {
                response.setStatus(429);
                response.getWriter().write("Too many login attempts");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}
