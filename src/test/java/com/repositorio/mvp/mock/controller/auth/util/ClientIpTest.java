package com.repositorio.mvp.mock.controller.auth.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.repositorio.mvp.infrastructure.security.util.ClientIp;

import jakarta.servlet.http.HttpServletRequest;

@ExtendWith(MockitoExtension.class)
public class ClientIpTest {
    @Mock
    private HttpServletRequest request;

    @Test
    public void getClientIp_WhenHeaderIsNull_ReturnsRemoteAddr() {
        when(request.getHeader("X-Forwarded-For")).thenReturn(null);
        when(request.getRemoteAddr()).thenReturn("192.168.1.100");

        String ip = ClientIp.getClientIp(request);

        assertEquals("192.168.1.100", ip);
    }

    @Test
    public void getClientIp_WhenHeaderIsEmpty_ReturnsRemoteAddr() {
        when(request.getHeader("X-Forwarded-For")).thenReturn("");
        when(request.getRemoteAddr()).thenReturn("10.0.0.5");

        String ip = ClientIp.getClientIp(request);

        assertEquals("10.0.0.5", ip);
    }

    @Test
    public void getClientIp_WhenHeaderIsUnknown_ReturnsRemoteAddr() {
        when(request.getHeader("X-Forwarded-For")).thenReturn("unknown");
        when(request.getRemoteAddr()).thenReturn("172.16.0.10");

        String ip = ClientIp.getClientIp(request);

        assertEquals("172.16.0.10", ip);
    }

    @Test
    public void getClientIp_WhenHeaderHasSingleIp_ReturnsThatIp() {
        when(request.getHeader("X-Forwarded-For")).thenReturn("203.0.113.50");

        String ip = ClientIp.getClientIp(request);

        assertEquals("203.0.113.50", ip);
        verify(request, never()).getRemoteAddr();
    }

    @Test
    public void getClientIp_WhenHeaderHasMultipleIps_ReturnsFirstIpTrimmed() {
        when(request.getHeader("X-Forwarded-For")).thenReturn("  198.51.100.22 , 10.0.0.1, 192.168.0.1 ");

        String ip = ClientIp.getClientIp(request);

        assertEquals("198.51.100.22", ip);
        verify(request, never()).getRemoteAddr();
    }
}