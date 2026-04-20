package com.repositorio.mvp.domain.auth.service.security;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import org.springframework.stereotype.Service;
import lombok.NonNull;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * Serviço de Rate Limiting para proteção da infraestrutura da API.
 * Implementa o algoritmo Token Bucket utilizando Bucket4j e Caffeine para
 * persistência temporária por IP.
 */
@Service
public class RateLimitingService {
    private final Cache<String, Bucket> cache = Caffeine.newBuilder()
            .expireAfterAccess(1, TimeUnit.HOURS)
            .maximumSize(10000)
            .build();

    private final Cache<String, Boolean> bannedIps = Caffeine.newBuilder()
            .expireAfterWrite(24, TimeUnit.HOURS)
            .maximumSize(5000)
            .build();

    /**
     * Resolve ou cria um Bucket de requisições associado a um determinado endereço
     * IP.
     * 
     * @param ip Endereço IP do cliente requisitante.
     * @return O Bucket associado ao IP para conferência de permissão.
     */
    public Bucket resolveBucket(@NonNull String ip) {
        return cache.get(ip, this::newBucket);
    }

    /**
     * Resolve ou cria um Bucket específico para o endpoint de cadastro de usuários.
     * Política mais rígida: 3 cadastros por hora por IP para prevenir spam.
     * 
     * @param ip Endereço IP do cliente.
     * @return Bucket de cadastro associado ao IP.
     */
    public Bucket resolveRegistrationBucket(@NonNull String ip) {
        return cache.get("REG_" + ip, k -> Bucket.builder()
                .addLimit(Bandwidth.classic(3, Refill.greedy(3, Duration.ofHours(1))))
                .build());
    }

    /**
     * Resolve ou cria um Bucket específico para o endpoint de login.
     * Política rígida: 5 tentativas por 15 minutos para prevenir brute-force.
     * 
     * @param ip Endereço IP do cliente.
     * @return Bucket de login associado ao IP.
     */
    public Bucket resolveLoginBucket(@NonNull String ip) {
        return cache.get("LOGIN_" + ip, k -> Bucket.builder()
                .addLimit(Bandwidth.classic(5, Refill.greedy(5, Duration.ofMinutes(15))))
                .build());
    }

    /**
     * Bane um endereço IP por 24 horas.
     * 
     * @param ip Endereço IP a ser banido.
     */
    public void banIp(@NonNull String ip) {
        bannedIps.put(ip, Boolean.TRUE);
    }

    /**
     * Verifica se um endereço IP está na lista de banimento.
     * 
     * @param ip Endereço IP do cliente.
     * @return True se o IP estiver banido.
     */
    public boolean isBanned(@NonNull String ip) {
        return bannedIps.getIfPresent(ip) != null;
    }

    /**
     * Configura um novo balde (Bucket) com as políticas padrão de vazão.
     * Atualmente configurado para permitir 50 requisições por minuto com recarga
     * gulosa.
     * 
     * @param ip IP do cliente (usado apenas como chave no cache).
     * @return Uma instância configurada de Bucket.
     */
    private Bucket newBucket(String ip) {
        Bandwidth limit = Bandwidth.classic(20,
                Refill.greedy(
                        20,
                        Duration.ofMinutes(1)));
        return Bucket.builder()
                .addLimit(limit)
                .build();
    }
}