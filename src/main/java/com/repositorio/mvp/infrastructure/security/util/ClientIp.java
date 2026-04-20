package com.repositorio.mvp.infrastructure.security.util;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Classe utilitária para tratamento de cabeçalhos de rede.
 */
public class ClientIp {
    public static String getClientIp(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null || xfHeader.isEmpty() || "unknown".equalsIgnoreCase(xfHeader)) {
            xfHeader = request.getHeader("X-Real-IP");
        }
        if (xfHeader == null || xfHeader.isEmpty() || "unknown".equalsIgnoreCase(xfHeader)) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0].trim();
    }
}
