package com.repositorio.mvp.service.auth;

import java.security.SecureRandom;
import java.time.LocalDateTime;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.repositorio.mvp.DTO.auth.LoginRequestDTO;
import com.repositorio.mvp.DTO.auth.Verify2FARequestDTO;
import com.repositorio.mvp.model.User;
import com.repositorio.mvp.repository.UserRepository;
import com.repositorio.mvp.service.interfaces.TwoFactorNotification;
import com.repositorio.mvp.service.login.LoginAttemptService;
import com.repositorio.mvp.service.token.TokenService;

import lombok.RequiredArgsConstructor;

/**
 * Serviço responsável por orquestrar a lógica de negócio da autenticação.
 * Garante a verificação segura de senhas, controle de ataques de força bruta e gestão do ciclo de vida do 2FA.
 */

@Service
@RequiredArgsConstructor
public class LoginService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;
    private final TwoFactorNotification twoFactorStrategy;
    private final LoginAttemptService loginAttemptService;
    
    private final SecureRandom secureRandom = new SecureRandom();

    /**
     * Processa a primeira etapa do login.
     * Verifica se o IP ou a conta estão bloqueados por excesso de tentativas,
     * valida a senha e aciona o envio do código de 2FA.
     * * @param loginRequest Dados de acesso (e-mail e senha).
     * @param ip Endereço IP do cliente requisitante.
     * @throws IllegalArgumentException Se as credenciais forem inválidas ou houver bloqueio de segurança.
     */
    @Transactional
    public void initiateLogin(LoginRequestDTO loginRequest, String ip) {
        if (loginAttemptService.isBlocked(ip) || loginAttemptService.isBlocked(loginRequest.email())){
            throw new IllegalArgumentException("Muitas tentativas falhas.");
        }
        
        User user = userRepository.findByEmail(loginRequest.email())
            .orElseThrow(() -> {
                loginAttemptService.loginFailed(ip);
                return new IllegalArgumentException("Credenciais inválidas.");
                });        

        if (!passwordEncoder.matches(loginRequest.password(), user.getPassword())) {
            loginAttemptService.loginFailed(ip);
            loginAttemptService.loginFailed(user.getEmail());
            throw new IllegalArgumentException("Credenciais inválidas.");
        }

        loginAttemptService.loginSucceeded(ip);
        loginAttemptService.loginSucceeded(user.getEmail());

        String code = generateRandomCode();
        user.generateTwoFactorCode(code, LocalDateTime.now().plusMinutes(5));
        userRepository.save(user);

        twoFactorStrategy.sendTwoFactorCode(user, code);
    }

    /**
     * Processa a segunda etapa do login verificando o código 2FA.
     * * @param verifyRequest Dados contendo o e-mail e o código digitado pelo usuário.
     * @param ip Endereço IP do cliente requisitante.
     * @return Uma String contendo o Token JWT assinado para a sessão.
     * @throws IllegalArgumentException Se o código for inválido, não bater ou estiver expirado.
     */
    @Transactional
    public String verify2FAAndGenerateToken(Verify2FARequestDTO verifyRequest, String ip) {
        if (loginAttemptService.isBlocked(ip)){
            throw new IllegalArgumentException("Muitas tentativas falhas. Tente novamente mais tarde.");
        }
        
        User user = userRepository.findByEmail(verifyRequest.email())
            .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));

        if (user.getTwoFactorCode() == null || !user.getTwoFactorCode().equals(verifyRequest.code())) {
            loginAttemptService.loginFailed(ip);
            throw new IllegalArgumentException("Código 2FA inválido.");
        }

        if (user.getTwoFactorExpiry().isBefore(LocalDateTime.now())) {
            user.clearTwoFactorCode();
            userRepository.save(user);
            throw new IllegalArgumentException("Código 2FA expirado.");
        }

        loginAttemptService.loginSucceeded(ip);
        user.clearTwoFactorCode();
        userRepository.save(user);
        
        return tokenService.generateToken(user.getId());
    }

    /**
     * Gera de forma criptograficamente segura um código numérico de 6 dígitos.
     * * @return String formatada com o código gerado.
     */
    private String generateRandomCode() {
        return String.format("%06d", secureRandom.nextInt(1000000));
    }
}