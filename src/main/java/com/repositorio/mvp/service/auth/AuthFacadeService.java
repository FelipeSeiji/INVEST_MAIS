package com.repositorio.mvp.service.auth;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.repositorio.mvp.DTO.auth.AuthenticationDTO;
import com.repositorio.mvp.DTO.auth.LoginResponseDTO;
import com.repositorio.mvp.DTO.register.RegisterDTO;
import com.repositorio.mvp.enums.UserRole;
import com.repositorio.mvp.model.User;
import com.repositorio.mvp.repository.UserRepository;
import com.repositorio.mvp.service.login.LoginAttemptService;
import com.repositorio.mvp.service.token.TokenServiceImpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j // Log estruturado
@Service
@RequiredArgsConstructor // Substitui o uso de @Autowired
public class AuthFacadeService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final TokenServiceImpl tokenService;
    private final PasswordEncoder passwordEncoder;
    private final LoginAttemptService loginAttemptService;
    private final ApplicationEventPublisher eventPublisher; // Para o padrão Observer

    @Transactional
    public LoginResponseDTO authenticate(AuthenticationDTO data, String ipAddress) {
        log.info("Tentativa de login iniciada para o usuário [{}] a partir do IP [{}]", data.email(), ipAddress);

        if (loginAttemptService.isBlocked(ipAddress)) {
            log.warn("Login bloqueado: IP [{}] excedeu o limite de tentativas", ipAddress);
            throw new RuntimeException("IP temporariamente bloqueado devido a muitas tentativas falhas.");
        }

        try {
            var usernamePassword = new UsernamePasswordAuthenticationToken(data.email(), data.password());
            var auth = this.authenticationManager.authenticate(usernamePassword);

            loginAttemptService.loginSucceeded(ipAddress);
            User user = (User) auth.getPrincipal();
            String token = tokenService.generateToken(user.getId());

            log.info("Login bem-sucedido para o usuário [{}]", data.email());
            return new LoginResponseDTO(token);

        } catch (Exception e) {
            loginAttemptService.loginFailed(ipAddress);
            log.error("Falha na autenticação para o usuário [{}]", data.email(), e);
            throw new RuntimeException("Credenciais inválidas");
        }
    }

    @Transactional
    public void register(RegisterDTO data) {
        log.info("Iniciando registro para o email [{}]", data.email());

        // Verificação utilizando Optional se o repositório retornar Optional
        if (userRepository.existsByEmail(data.email())) {
            log.warn("Tentativa de registro com email já existente: [{}]", data.email());
            throw new IllegalArgumentException("Email já está em uso");
        }

        // Aplicação do padrão Builder
        User newUser = User.builder()
                .name(data.name())
                .email(data.email())
                .password(passwordEncoder.encode(data.password()))
                .role(UserRole.USER)
                .build();

        userRepository.save(newUser);
        log.info("Usuário [{}] registrado com sucesso", data.email());
    }
}