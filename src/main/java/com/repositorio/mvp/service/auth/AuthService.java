package com.repositorio.mvp.service.auth;

import com.repositorio.mvp.repository.token.InvalidTokenRepository;
import com.repositorio.mvp.repository.token.PasswordResetTokenRepository;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.repositorio.mvp.DTO.auth.LoginRequestDTO;
import com.repositorio.mvp.DTO.auth.Verify2FARequestDTO;
import com.repositorio.mvp.model.User;
import com.repositorio.mvp.model.token.InvalidToken;
import com.repositorio.mvp.model.token.PasswordResetToken;
import com.repositorio.mvp.repository.UserRepository;
import com.repositorio.mvp.service.interfaces.TwoFactorNotification;
import com.repositorio.mvp.service.login.LoginAttemptService;
import com.repositorio.mvp.service.token.TokenService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final InvalidTokenRepository invalidTokenRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final TwoFactorNotification twoFactorStrategy;
    private final LoginAttemptService loginAttemptService;
    private final SecureRandom secureRandom = new SecureRandom();
    private final PasswordResetTokenRepository passwordResetTokenRepository;

    @Transactional
    public void initiateLogin(LoginRequestDTO loginRequest, String ip) {
        if (loginAttemptService.isBlocked(ip)){
            throw new IllegalArgumentException("Muitas tentativas falhas");
        }
        
        User user = userRepository.findByEmail(loginRequest.email())
                .orElseThrow(() -> new IllegalArgumentException("Credenciais inválidas."));

        if (!passwordEncoder.matches(loginRequest.password(), user.getPassword())) {
            throw new IllegalArgumentException("Credenciais inválidas.");
        }

        loginAttemptService.loginSucceeded(ip);
        
        String code = generateRandomCode();
        user.generateTwoFactorCode(code, LocalDateTime.now().plusMinutes(5));
        userRepository.save(user);

        twoFactorStrategy.sendTwoFactorCode(user, code);
    ;
    }

    @Transactional
    public String verify2FAAndGenerateToken(Verify2FARequestDTO verifyRequest) {
        User user = userRepository.findByEmail(verifyRequest.email())
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));

        if (user.getTwoFactorCode() == null || !user.getTwoFactorCode().equals(verifyRequest.code())) {
            throw new IllegalArgumentException("Código 2FA inválido.");
        }

        if (user.getTwoFactorExpiry().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Código 2FA expirado.");
        }

        user.clearTwoFactorCode();
        userRepository.save(user);
        
        return tokenService.generateToken(user.getId());
    }

    private String generateRandomCode() {
        return String.format("%06d", secureRandom.nextInt(999999));
    }

    public void logout(String token){
        String tokenJWT = token.replace("Bearer ","");
        InvalidToken invalidToken = new InvalidToken(tokenJWT, tokenService.getExpiration(tokenJWT));
        invalidTokenRepository.save(invalidToken);
    }

    @Transactional
    public void createPasswordResetTokenForUser(String email) {
        userRepository.findByEmail(email).ifPresent(user -> {
            String token = UUID.randomUUID().toString();
            PasswordResetToken myToken = new PasswordResetToken(token, user);
            passwordResetTokenRepository.save(myToken);
        });
    }

    @Transactional
    public void resetPassword(String token, String newPassword) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token)
                .orElseThrow(() -> {
                    return new IllegalArgumentException("Token inválido.");
                });

        if (resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {
            passwordResetTokenRepository.delete(resetToken);
            throw new IllegalArgumentException("Token expirado.");
        }

        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        
        passwordResetTokenRepository.delete(resetToken);
    }
}