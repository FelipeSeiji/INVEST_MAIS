package com.repositorio.mvp.domain.auth.service.login;

import java.time.LocalDateTime;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.repositorio.mvp.domain.auth.DTO.LoginRequestDTO;
import com.repositorio.mvp.domain.auth.DTO.Verify2FARequestDTO;
import com.repositorio.mvp.domain.user.model.User;
import com.repositorio.mvp.domain.user.repository.UserRepository;
import com.repositorio.mvp.domain.auth.service.interfaces.TwoFactorNotification;
import com.repositorio.mvp.domain.auth.service.token.TokenProvider;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Serviço responsável por orquestrar a lógica de negócio da autenticação.
 * Garante a verificação segura de senhas, controle de ataques de força bruta e
 * gestão do ciclo de vida do 2FA.
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class LoginService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenProvider tokenProvider;
    private final TwoFactorService twoFactorService;
    private final TwoFactorNotification twoFactorStrategy;
    private final LoginAttemptService loginAttemptService;

    /**
     * Processa a primeira etapa do login.
     * Verifica se o IP ou a conta estão bloqueados por excesso de tentativas,
     * valida a senha e aciona o envio do código de 2FA.
     * * @param loginRequest Dados de acesso (e-mail e senha).
     * 
     * @param ip Endereço IP do cliente requisitante.
     * @throws IllegalArgumentException Se as credenciais forem inválidas ou houver
     *                                  bloqueio de segurança.
     */
    @Transactional
    public void initiateLogin(LoginRequestDTO loginRequest, String ip) {
        if (loginAttemptService.isBlocked(ip) || loginAttemptService.isBlocked(loginRequest.email())) {
            log.warn("ALERTA: Tentativa de login bloqueada (Força Bruta). IP: {} | Conta alvo: {}", ip,
                    loginRequest.email());
            throw new IllegalArgumentException("Muitas tentativas falhas.");
        }

        String searchHash = org.apache.commons.codec.digest.DigestUtils.sha256Hex(loginRequest.email().toLowerCase());
        User user = userRepository.findBySecurityEmailHash(searchHash)
                .orElseThrow(() -> {
                    loginAttemptService.loginFailed(ip);
                    log.warn("FALHA DE LOGIN: Usuário não existe. IP: {} | E-mail tentado: {}", ip,
                            maskEmail(loginRequest.email()));
                    return new IllegalArgumentException("Credenciais inválidas.");
                });

        if (!passwordEncoder.matches(loginRequest.password(), user.getSecurity().getPassword())) {
            loginAttemptService.loginFailed(ip);
            loginAttemptService.loginFailed(user.getEmail());
            log.warn("FALHA DE LOGIN: Senha incorreta. IP: {} | E-mail: {}", ip, user.getEmail());
            throw new IllegalArgumentException("Credenciais inválidas.");
        }

        loginAttemptService.loginSucceeded(ip);
        loginAttemptService.loginSucceeded(user.getEmail());

        log.info("LOGIN FASE 1: Credenciais válidas. Gerando 2FA para o usuário {}. IP: {}", user.getId(), ip);

        twoFactorService.prepareTwoFactor(user);
        userRepository.save(user);

        twoFactorStrategy.sendTwoFactorCode(user, user.getSecurity().getTwoFactorCode());
    }

    /**
     * Processa a segunda etapa do login verificando o código 2FA.
     * * @param verifyRequest Dados contendo o e-mail e o código digitado pelo
     * usuário.
     * 
     * @param ip Endereço IP do cliente requisitante.
     * @return Uma String contendo o Token JWT assinado para a sessão.
     * @throws IllegalArgumentException Se o código for inválido, não bater ou
     *                                  estiver expirado.
     */
    @Transactional
    public String verify2FAAndGenerateToken(Verify2FARequestDTO verifyRequest, String ip) {
        if (loginAttemptService.isBlocked(ip) || loginAttemptService.isBlocked("2FA:" + verifyRequest.email())) {
            log.warn("ALERTA: Tentativa de 2FA bloqueada (Força Bruta). IP: {} | Conta alvo: {}", ip,
                    verifyRequest.email());
            throw new IllegalArgumentException("Muitas tentativas falhas. Tente novamente mais tarde.");
        }

        String searchHash = org.apache.commons.codec.digest.DigestUtils.sha256Hex(verifyRequest.email().toLowerCase());
        User user = userRepository.findBySecurityEmailHash(searchHash)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado."));

        if (user.getSecurity().getTwoFactorCode() == null || !user.getSecurity().getTwoFactorCode().equals(verifyRequest.code())) {
            loginAttemptService.loginFailed(ip);
            loginAttemptService.loginFailed("2FA:" + user.getEmail());
            log.warn("FALHA 2FA: Código inválido. IP: {} | E-mail: {}", ip, maskEmail(user.getEmail()));
            throw new IllegalArgumentException("Código 2FA inválido.");
        }

        if (user.getSecurity().getTwoFactorExpiry().isBefore(LocalDateTime.now())) {
            user.getSecurity().clearTwoFactorCode();
            userRepository.save(user);
            log.warn("FALHA 2FA: Código expirado. IP: {} | E-mail: {}", ip, user.getEmail());
            throw new IllegalArgumentException("Código 2FA expirado.");
        }

        loginAttemptService.loginSucceeded(ip);
        user.getSecurity().clearTwoFactorCode();
        userRepository.save(user);

        log.info("LOGIN SUCESSO: 2FA validado. Token JWT emitido para o usuário {}. IP: {}", user.getId(), ip);

        return tokenProvider.generateToken(user.getId());
    }



    private String maskEmail(String email) {
        if (email == null || !email.contains("@"))
            return "***";
        String[] parts = email.split("@");
        return parts[0].substring(0, Math.min(2, parts[0].length())) + "***@" + parts[1];
    }
}