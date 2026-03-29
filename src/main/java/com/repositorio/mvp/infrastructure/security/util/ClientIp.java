package com.repositorio.mvp.infrastructure.security.util;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Classe utilitária para tratamento de cabeçalhos de rede.
 */
public class ClientIp {
    /**
     * Extrai o endereço IP real do cliente que originou a requisição.
     * Essencial para ambientes onde a aplicação roda atrás de Proxies Reversos (como Nginx) ou Load Balancers,
     * onde o getRemoteAddr() retornaria incorretamente o IP da infraestrutura local em vez do atacante/usuário.
     * * @param request Requisição HTTP em trânsito.
     * @return String contendo o IP real do cliente.
     */
    public static String getClientIp(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Forwarded-For");

        if (ipAddress != null && !ipAddress.isEmpty() && !"unknown".equalsIgnoreCase(ipAddress)) {
            String cleanIp = ipAddress.split(",")[0].trim();
            
            if (cleanIp.length() <= 45) { 
                return cleanIp;
            }
        }

        return request.getRemoteAddr();
    }
}
