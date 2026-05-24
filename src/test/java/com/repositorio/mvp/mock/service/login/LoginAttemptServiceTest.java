package com.repositorio.mvp.mock.service.login;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import com.repositorio.mvp.domain.auth.login.service.LoginAttemptService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


@ExtendWith(MockitoExtension.class)
public class LoginAttemptServiceTest {
    private LoginAttemptService loginAttemptService;

    private final String IP = "182.168.0.1";

    @BeforeEach
    void setUp() {
        loginAttemptService = new LoginAttemptService();
    }

    @Test
    public void loginFailed_IncrementsAttemptsCounter() {
        loginAttemptService.loginFailed(IP);

        assertEquals(1, loginAttemptService.getAttempts(IP));
        assertFalse(loginAttemptService.isBlocked(IP));
    }

    @Test
    public void loginFailed_ReachesMaxAttempts_BlocksTheKey() {
        for (int i = 0; i < 5; i++) {
            loginAttemptService.loginFailed(IP);
        }

        assertEquals(5, loginAttemptService.getAttempts(IP));
        assertTrue(loginAttemptService.isBlocked(IP), "A chave deveria estar bloqueada após 5 tentativas falhas");
    }

    @Test
    public void loginSucceeded_ResetsAttemptsAndRemovesBlock() {
        loginAttemptService.loginFailed(IP);
        loginAttemptService.loginFailed(IP);
        loginAttemptService.loginFailed(IP);
        assertEquals(3, loginAttemptService.getAttempts(IP));

        loginAttemptService.loginSucceeded(IP);

        assertEquals(0, loginAttemptService.getAttempts(IP));
        assertFalse(loginAttemptService.isBlocked(IP));
    }
}
