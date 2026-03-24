package com.repositorio.mvp.mock.service.auth;

import java.security.SecureRandom;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

import com.repositorio.mvp.DTO.auth.LoginRequestDTO;
import com.repositorio.mvp.DTO.auth.Verify2FARequestDTO;
import com.repositorio.mvp.model.User;
import com.repositorio.mvp.repository.UserRepository;
import com.repositorio.mvp.service.auth.LoginService;
import com.repositorio.mvp.service.interfaces.TwoFactorNotification;
import com.repositorio.mvp.service.login.LoginAttemptService;
import com.repositorio.mvp.service.token.TokenService;

import org.mockito.InjectMocks;
import org.mockito.Mock;

@ExtendWith(MockitoExtension.class)
public class LoginServiceTest {

    @InjectMocks
    private LoginService loginService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private TokenService tokenService;

    @Mock
    private TwoFactorNotification twoFactorStrategy;

    @Mock
    private LoginAttemptService loginAttemptService;
    
    private SecureRandom secureRandom = new SecureRandom();

   @Test
    public void initiateLogin_WithValidData_ReturnsToken() {

    }

    @Test
    public void initiateLogin_WithInvalidData_ThrowsException() {
        
    }


    @Test
    public String verify2FAAndGenerateToken_With() {
        return null;
    }

    private String generateRandomCode() {
        return String.format("%06d", secureRandom.nextInt(1000000));
    }
}