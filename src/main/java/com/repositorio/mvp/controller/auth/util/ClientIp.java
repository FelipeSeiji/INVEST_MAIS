package com.repositorio.mvp.controller.auth.util;

import jakarta.servlet.http.HttpServletRequest;

public class ClientIp {
    public static String getClientIp(HttpServletRequest request) {
        String ipAddress = request.getHeader("X-Forwarded-For");

        if(ipAddress == null || ipAddress.isEmpty() || "unknown".equalsIgnoreCase(ipAddress)) {
            return request.getRemoteAddr();
        } else {
            return ipAddress.split(",")[0].trim();
        }
    }
}
