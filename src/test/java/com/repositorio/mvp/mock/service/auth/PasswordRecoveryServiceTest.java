package com.repositorio.mvp.mock.service.auth;

import java.time.LocalDateTime;
import java.util.Optional;

import com.repositorio.mvp.common.security.CryptoService;
import com.repositorio.mvp.domain.auth.password.service.RecoveryEmailService;
import com.repositorio.mvp.domain.auth.login.service.LoginAttemptService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.repositorio.mvp.common.result.ServiceResult;
import com.repositorio.mvp.domain.auth.password.model.PasswordResetToken;
import com.repositorio.mvp.domain.auth.password.repository.PasswordResetTokenRepository;
import com.repositorio.mvp.domain.auth.password.service.PasswordRecoveryService;
import com.repositorio.mvp.domain.user.model.User;
import com.repositorio.mvp.domain.user.repository.UserRepository;
import com.repositorio.mvp.shared.UserConstants;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PasswordRecoveryServiceTest {

    @InjectMocks
    private PasswordRecoveryService passwordRecoveryService;

    @Mock
    private UserRepository userRepository;
    
    @Mock
    private PasswordResetTokenRepository passwordResetTokenRepository;
    
    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private RecoveryEmailService authEmailService;

    @Mock
    private TransactionTemplate transactionTemplate;

    @Mock
    private CryptoService cryptoService;

    @Mock
    private LoginAttemptService loginAttemptService;

    private User mockUser;

    @BeforeEach
    void setUp() {
        mockUser = UserConstants.createMockUser();
    }
    
    @Test
    public void createToken_WhenUserExists_GeneratesAndSavesToken() {
        String email = "felipe@email.com";
        String expectedHash = "hashed_email";

        when(cryptoService.generateSha256Hash(email)).thenReturn(expectedHash);
        when(userRepository.findBySecurityEmailHash(expectedHash)).thenReturn(Optional.of(mockUser));
        when(cryptoService.generateSecureToken()).thenReturn("raw_token");
        when(cryptoService.generateHmacTokenHash("raw_token")).thenReturn("hmac_token");

        when(transactionTemplate.execute(any())).thenAnswer(invocation -> {
            TransactionCallback callback = invocation.getArgument(0);
            return callback.doInTransaction(null);
        });

        passwordRecoveryService.createPasswordResetTokenForUser(email);

        verify(passwordResetTokenRepository).save(any(PasswordResetToken.class));
        verify(authEmailService).sendPasswordRecoveryEmail(eq(email), eq(mockUser.getName()), eq("raw_token"));
    }

    @Test
    public void createToken_WhenUserDoesNotExist_DoesNothing() {
        String email = "not_exist@gmail.com";
        String expectedHash = "hashed_email";
        
        when(cryptoService.generateSha256Hash(email)).thenReturn(expectedHash);
        when(userRepository.findBySecurityEmailHash(expectedHash)).thenReturn(Optional.empty());

        passwordRecoveryService.createPasswordResetTokenForUser(email);

        verify(passwordResetTokenRepository, never()).save(any());
        verify(authEmailService, never()).sendPasswordRecoveryEmail(anyString(), anyString(), anyString());
    }

    @Test
    public void resetPassword_WithValidToken_UpdatesPasswordAndDeletesToken() {
        String tokenInput = "valid_token";
        String hmacToken = "hmac_valid_token";
        String newPassword = "newPassword@123";
        String encodedPassword = "encodedPassword";

        when(cryptoService.generateHmacTokenHash(tokenInput)).thenReturn(hmacToken);

        PasswordResetToken passwordResetToken = new PasswordResetToken(hmacToken, "hmac-v1", mockUser);
        passwordResetToken.setExpiryDate(LocalDateTime.now().plusMinutes(10));

        when(passwordResetTokenRepository.findByToken(hmacToken))
                .thenReturn(Optional.of(passwordResetToken));
                
        when(passwordEncoder.encode(newPassword)).thenReturn(encodedPassword);

        passwordRecoveryService.resetPassword(tokenInput, newPassword);

        assertEquals(encodedPassword, mockUser.getSecurity().getPassword());
        verify(userRepository).save(mockUser);
        verify(passwordResetTokenRepository).delete(passwordResetToken);
    }

    @Test
    public void resetPassword_WithInvalidToken_ReturnsError() {
        String invalidToken = "invalid_token";
        String hmacToken = "hmac_invalid_token";
        String newPassword = "newPassword@123";
        
        when(cryptoService.generateHmacTokenHash(invalidToken)).thenReturn(hmacToken);
        when(passwordResetTokenRepository.findByToken(hmacToken)).thenReturn(Optional.empty());

        ServiceResult<Void> result = passwordRecoveryService.resetPassword(invalidToken, newPassword);

        assertTrue(result instanceof ServiceResult.Error);
        assertEquals("Token inválido ou não encontrado.", ((ServiceResult.Error<Void>) result).message());

        verify(userRepository, never()).save(any());
    }

    @Test
    public void resetPassword_WithExpiredToken_ReturnsErrorAndDeletesToken() {
        String tokenInput = "token_expirado";
        String hmacToken = "hmac_expired_token";
        String newPassword = "newPassword@123";   
        
        when(cryptoService.generateHmacTokenHash(tokenInput)).thenReturn(hmacToken);

        PasswordResetToken expiredToken = new PasswordResetToken(hmacToken, "hmac-v1", mockUser);
        expiredToken.setExpiryDate(LocalDateTime.now().minusMinutes(5));

        when(passwordResetTokenRepository.findByToken(hmacToken)).thenReturn(Optional.of(expiredToken));

        ServiceResult<Void> result = passwordRecoveryService.resetPassword(tokenInput, newPassword);

        assertTrue(result instanceof ServiceResult.Error);
        assertTrue(((ServiceResult.Error<Void>) result).message().contains("expirado"));

        verify(passwordResetTokenRepository).delete(expiredToken);
        verify(userRepository, never()).save(any());
    }
}