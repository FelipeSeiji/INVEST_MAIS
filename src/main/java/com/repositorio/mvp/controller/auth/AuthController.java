package com.repositorio.mvp.controller.auth;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import com.repositorio.mvp.DTO.auth.TokenResponseDTO;
import com.repositorio.mvp.DTO.auth.Verify2FARequestDTO;
import com.repositorio.mvp.DTO.auth.LoginRequestDTO;
import com.repositorio.mvp.DTO.auth.ResetPasswordRequestDTO; 
import com.repositorio.mvp.DTO.common.MessageResponseDTO;
import com.repositorio.mvp.controller.auth.util.ClientIp;
import com.repositorio.mvp.service.auth.LoginService;
import com.repositorio.mvp.service.auth.PasswordRecoveryService;
import com.repositorio.mvp.service.auth.SessionService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final LoginService loginService;
    private final SessionService sessionService;
    private final PasswordRecoveryService passwordRecoveryService;

    // POST /auth/login
    @PostMapping("/login")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Inicia o login e envia código 2FA", description = "Valida e-mail e senha. Se corretos, envia um e-mail com o código de 6 dígitos para o usuário.")
    public MessageResponseDTO login(@Valid @RequestBody LoginRequestDTO loginRequest, HttpServletRequest request) {
        String ip = ClientIp.getClientIp(request);
        loginService.initiateLogin(loginRequest, ip);

        return new MessageResponseDTO("Código de verificação enviado para o seu e-mail.");
    }

    // POST /auth/verify-2fa
    @PostMapping("/verify-2fa")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Valida o código 2FA e retorna o JWT", description = "Valida o código recebido por e-mail. Se correto e no prazo, devolve o token de acesso (JWT).")
    public TokenResponseDTO verify2FA(@Valid @RequestBody Verify2FARequestDTO verifyRequest) {
        String token = loginService.verify2FAAndGenerateToken(verifyRequest);
        return new TokenResponseDTO(token);
    }

    // POST /auth/logout
    @PostMapping("/logout")
    @ResponseStatus(HttpStatus.NO_CONTENT) 
    @Operation(summary = "Realiza o logout do usuário", description = "Invalida o token JWT fornecido no cabeçalho de autorização, encerrando a sessão ativa.")
    public void logout(@RequestHeader("Authorization") String token) {
        SecurityContextHolder.clearContext();
        sessionService.logout(token);  
    }

    // POST /auth/forgot-password
    @PostMapping("/forgot-password")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Solicita a recuperação de senha", description = "Gera um token de redefinição e envia um link para o e-mail informado.")
    public MessageResponseDTO forgotPassword(@RequestParam String email) {
        passwordRecoveryService.createPasswordResetTokenForUser(email);
        return new MessageResponseDTO("Se o e-mail existir, um link de recuperação foi enviado.");
    }

    // POST /auth/reset-password
    @PostMapping("/reset-password")
    @ResponseStatus(HttpStatus.OK)
    @Operation(summary = "Redefine a senha do usuário", description = "Recebe o token de recuperação e a nova senha para atualizar as credenciais.")
    public MessageResponseDTO resetPassword(@Valid @RequestBody ResetPasswordRequestDTO request) {
        passwordRecoveryService.resetPassword(request.token(), request.newPassword());
        return new MessageResponseDTO("Senha redefinida com sucesso.");
    }
}