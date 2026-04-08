package com.repositorio.mvp.mock.service.auth;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.apache.commons.codec.digest.DigestUtils;

import com.repositorio.mvp.domain.auth.DTO.LoginRequestDTO;
import com.repositorio.mvp.domain.auth.DTO.Verify2FARequestDTO;
import com.repositorio.mvp.domain.user.model.User;
import com.repositorio.mvp.domain.user.repository.UserRepository;
import com.repositorio.mvp.domain.auth.service.interfaces.TwoFactorNotification;
import com.repositorio.mvp.domain.auth.service.login.LoginAttemptService;
import com.repositorio.mvp.domain.auth.service.login.LoginService;
import com.repositorio.mvp.domain.auth.service.login.TwoFactorService;
import com.repositorio.mvp.domain.auth.service.token.TokenService;
import com.repositorio.mvp.shared.UserConstants;

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
    private TwoFactorService twoFactorService;

    @Mock
    private TwoFactorNotification twoFactorStrategy;

    @Mock
    private LoginAttemptService loginAttemptService;

    private User mockUser;
    private final String MOCK_IP = "192.168.1.10";

    @BeforeEach
    void setUp() {
        mockUser = UserConstants.createMockUser(); 
    }

   @Test
    public void initiateLogin_WithValidCredentials_GeneratesAndSends2FA() {
        LoginRequestDTO loginRequestDTO = new LoginRequestDTO(UserConstants.USER.email(), UserConstants.USER.password());

        when(loginAttemptService.isBlocked(MOCK_IP)).thenReturn(false);
        when(loginAttemptService.isBlocked(loginRequestDTO.email())).thenReturn(false);
        when(userRepository.findBySecurityEmailHash(DigestUtils.sha256Hex(loginRequestDTO.email().toLowerCase()))).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches(loginRequestDTO.password(), mockUser.getSecurity().getPassword())).thenReturn(true);

        doAnswer(invocation -> {
        User u = invocation.getArgument(0);
        u.getSecurity().generateTwoFactorCode("123456", LocalDateTime.now().plusMinutes(5));
        return null;
    }).when(twoFactorService).prepareTwoFactor(any(User.class));

    assertDoesNotThrow(() -> loginService.initiateLogin(loginRequestDTO, MOCK_IP));

    verify(twoFactorService).prepareTwoFactor(mockUser); 
    verify(twoFactorStrategy).sendTwoFactorCode(eq(mockUser), anyString());
    
    assertNotNull(mockUser.getSecurity().getTwoFactorCode());
    }

    @Test
    public void initiateLogin_WithInvalidPassword_ThrowsExceptionAndRegistersFailure() {
        LoginRequestDTO loginRequestDTO = new LoginRequestDTO(UserConstants.USER.email(), "invalidPassword");

        when(loginAttemptService.isBlocked(MOCK_IP)).thenReturn(false);
        when(loginAttemptService.isBlocked(loginRequestDTO.email())).thenReturn(false);
        when(userRepository.findBySecurityEmailHash(DigestUtils.sha256Hex(loginRequestDTO.email().toLowerCase()))).thenReturn(Optional.of(mockUser));
        when(passwordEncoder.matches(loginRequestDTO.password(), mockUser.getSecurity().getPassword())).thenReturn(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            loginService.initiateLogin(loginRequestDTO, MOCK_IP);
        });

        assertEquals("Credenciais inválidas.", exception.getMessage());

        verify(loginAttemptService).loginFailed(MOCK_IP);
        verify(loginAttemptService).loginFailed(loginRequestDTO.email());

        verify(twoFactorStrategy, never()).sendTwoFactorCode(any(), any());
    }

    @Test
    public void initiateLogin_WhenIpIsBlocked_ThrowsException() {
        LoginRequestDTO loginRequestDTO = new LoginRequestDTO(UserConstants.USER.email(), UserConstants.USER.password());
        when(loginAttemptService.isBlocked(MOCK_IP)).thenReturn(true);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            loginService.initiateLogin(loginRequestDTO, MOCK_IP);
        });

        assertTrue(exception.getMessage().contains("Muitas tentativas falhas"));

        verify(userRepository, never()).findBySecurityEmailHash(anyString());
    }

    @Test
    public void verify2FA_WithValidCode_ReturnsJwtToken() {
        String validCode = "123456";
        mockUser.getSecurity().generateTwoFactorCode(validCode, LocalDateTime.now().plusMinutes(5));
        
        Verify2FARequestDTO verify2faRequestDTO = new Verify2FARequestDTO(mockUser.getEmail(), validCode);
        String expectedJwt = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.mocked_token";

        when(loginAttemptService.isBlocked(MOCK_IP)).thenReturn(false);
        when(userRepository.findBySecurityEmailHash(DigestUtils.sha256Hex(verify2faRequestDTO.email().toLowerCase()))).thenReturn(Optional.of(mockUser));
        when(tokenService.generateToken(mockUser.getId())).thenReturn(expectedJwt);

        String actualJwt = loginService.verify2FAAndGenerateToken(verify2faRequestDTO, MOCK_IP);

        assertEquals(expectedJwt, actualJwt);
        assertNull(mockUser.getSecurity().getTwoFactorCode());
        verify(loginAttemptService).loginSucceeded(MOCK_IP);
        verify(userRepository).save(mockUser);
    }
    @Test
    public void verify2FA_WithInvalidCode_ThrowsExceptionAndRegistersFailure() {
        mockUser.getSecurity().generateTwoFactorCode("123456", LocalDateTime.now().plusMinutes(5));
        Verify2FARequestDTO request = new Verify2FARequestDTO(mockUser.getEmail(), "999999"); 
        
        when(loginAttemptService.isBlocked(MOCK_IP)).thenReturn(false);
        when(userRepository.findBySecurityEmailHash(DigestUtils.sha256Hex(request.email().toLowerCase()))).thenReturn(Optional.of(mockUser));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            loginService.verify2FAAndGenerateToken(request, MOCK_IP);
        });

        assertEquals("Código 2FA inválido.", exception.getMessage());
        verify(loginAttemptService).loginFailed(MOCK_IP); 
        verify(tokenService, never()).generateToken(any());
    }

    @Test
    public void verify2FA_WithExpiredCode_ThrowsException() {
        String validCode = "123456";

        mockUser.getSecurity().generateTwoFactorCode(validCode, LocalDateTime.now().minusMinutes(1)); 

        Verify2FARequestDTO request = new Verify2FARequestDTO(mockUser.getEmail(), validCode);
        
        when(loginAttemptService.isBlocked(MOCK_IP)).thenReturn(false);
        when(userRepository.findBySecurityEmailHash(DigestUtils.sha256Hex(request.email().toLowerCase()))).thenReturn(Optional.of(mockUser));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            loginService.verify2FAAndGenerateToken(request, MOCK_IP);
        });

        assertTrue(exception.getMessage().contains("expirado"));
        assertNull(mockUser.getSecurity().getTwoFactorCode()); 
        verify(userRepository).save(mockUser);
    }
}