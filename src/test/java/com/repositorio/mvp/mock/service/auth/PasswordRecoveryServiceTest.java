package com.repositorio.mvp.mock.service.auth;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.Optional;

import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.repositorio.mvp.domain.auth.model.PasswordResetToken;
import com.repositorio.mvp.domain.auth.repository.PasswordResetTokenRepository;
import com.repositorio.mvp.domain.user.model.User;
import com.repositorio.mvp.domain.user.repository.UserRepository;
import com.repositorio.mvp.domain.auth.service.auth.PasswordRecoveryService;
import com.repositorio.mvp.shared.UserConstants;

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
    private JavaMailSender mailSender;

    @Mock
    private TransactionTemplate transactionTemplate;

    private User mockUser;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(passwordRecoveryService, "tokenSecret", "my_secret_token_key");
        mockUser = UserConstants.createMockUser();
    }
    
    @Test
    public void createToken_WhenUserExists_GeneratesAndSavesToken() {
        String email = "felipe@email.com";
        String expectedHash = org.apache.commons.codec.digest.DigestUtils.sha256Hex(email.toLowerCase());

        when(userRepository.findBySecurityEmailHash(expectedHash)).thenReturn(Optional.of(mockUser));
        when(transactionTemplate.execute(any())).thenAnswer(invocation -> {
            TransactionCallback callback = invocation.getArgument(0);
            return callback.doInTransaction(null);
        });

        passwordRecoveryService.createPasswordResetTokenForUser(email);

        verify(passwordResetTokenRepository).save(any(PasswordResetToken.class));
    }

    @Test
    public void createToken_WhenUserDoesNotExist_DoesNothing() {
        String email = "not_exist@gmail.com";
        String expectedHash = DigestUtils.sha256Hex(email.toLowerCase());
        
        when(userRepository.findBySecurityEmailHash(expectedHash)).thenReturn(Optional.empty());

        passwordRecoveryService.createPasswordResetTokenForUser(email);

        verify(passwordResetTokenRepository, never()).save(any());
    }

    @Test
    public void resetPassword_WithValidToken_UpdatesPasswordAndDeletesToken() {
        String tokenInput = "valid_token";
        String newPassword = "newPassword@123";
        String encodedPassword = "encodedPassword";

        PasswordResetToken passwordResetToken = new PasswordResetToken(tokenInput, "hmac-v1", mockUser);
        passwordResetToken.setExpiryDate(LocalDateTime.now().plusMinutes(10));

        when(passwordResetTokenRepository.findByToken(anyString()))
                .thenReturn(Optional.of(passwordResetToken));
                
        when(passwordEncoder.encode(newPassword)).thenReturn(encodedPassword);

        passwordRecoveryService.resetPassword(tokenInput, newPassword);

        assertEquals(encodedPassword, mockUser.getSecurity().getPassword());
        verify(userRepository).save(mockUser);
        verify(passwordResetTokenRepository).delete(passwordResetToken);
    }

    @Test
    public void resetPassword_WithInvalidToken_ThrowsException() {
        String invalidToken = "invalid_token";
        String newPassword = "newPassword@123";
        
        when(passwordResetTokenRepository.findByToken(anyString())).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            passwordRecoveryService.resetPassword(invalidToken, newPassword);
        });

        assertEquals("Token inválido ou não encontrado.", exception.getMessage());

        verify(userRepository, never()).save(any());
    }

    @Test
    public void resetPassword_WithExpiredToken_ThrowsExceptionAndDeletesToken() {
        String token = "token_expirado";
        String newPassword = "newPassword@123";   
        PasswordResetToken expiredToken = new PasswordResetToken(token, "hmac-v1", mockUser);

        expiredToken.setExpiryDate(LocalDateTime.now().minusMinutes(5));

        when(passwordResetTokenRepository.findByToken(anyString())).thenReturn(Optional.of(expiredToken));

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            passwordRecoveryService.resetPassword(token, newPassword);
        });

        assertTrue(exception.getMessage().contains("expirado"));

        verify(passwordResetTokenRepository).delete(expiredToken);
        verify(userRepository, never()).save(any());
    }
}