package com.repositorio.mvp.domain.auth.service.security;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * Serviço de Rate Limiting para proteção da infraestrutura da API.
 * Implementa o algoritmo Token Bucket utilizando Bucket4j e Caffeine para persistência temporária por IP.
 */
@Service
public class RateLimitingService {
    private final Cache<String, Bucket> cache = Caffeine.newBuilder()
        .expireAfterAccess(1, TimeUnit.HOURS)
        .maximumSize(10000)
        .build();

    /**
     * Resolve ou cria um Bucket de requisições associado a um determinado endereço IP.
     * 
     * @param ip Endereço IP do cliente requisitante.
     * @return O Bucket associado ao IP para conferência de permissão.
     */
    public Bucket resolveBucket(String ip) {
        return cache.get(ip, this::newBucket);
    }

    /**
     * Configura um novo balde (Bucket) com as políticas padrão de vazão.
     * Atualmente configurado para permitir 50 requisições por minuto com recarga gulosa.
     * 
     * @param ip IP do cliente (usado apenas como chave no cache).
     * @return Uma instância configurada de Bucket.
     */
    private Bucket newBucket(String ip) {
        Bandwidth limit = Bandwidth.classic(50, 
            Refill.greedy(
                50, 
                Duration.ofMinutes(1)
            )
        );
        return Bucket.builder()
            .addLimit(limit)
            .build();
    }
}