package com.repositorio.mvp.controller.auth;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.repositorio.mvp.DTO.auth.LoginRequestDTO;
import com.repositorio.mvp.DTO.auth.TokenResponseDTO;
import com.repositorio.mvp.DTO.auth.Verify2FARequestDTO;
import com.repositorio.mvp.service.auth.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final HttpServletRequest request;

    private String getClientIp(HttpServletRequest httpServletRequest) {
        return httpServletRequest.getRemoteAddr();
    }

    // POST /auth/login
    @PostMapping("/login")
    @Operation(summary = "Inicia o login e envia código 2FA", description = "Valida e-mail e senha. Se corretos, envia um e-mail com o código de 6 dígitos para o usuário.")
    public ResponseEntity<String> login(@Valid @RequestBody LoginRequestDTO loginRequest, HttpServletRequest httpServletRequest) {
        String ip = httpServletRequest.getRemoteAddr();
        authService.initiateLogin(loginRequest, ip);

        return ResponseEntity.ok("Código de verificação enviado para o seu e-mail.");
    }

    // POST /auth/verify-2fa
    @PostMapping("/verify-2fa")
    @Operation(summary = "Valida o código 2FA e retorna o JWT", description = "Valida o código recebido por e-mail. Se correto e no prazo, devolve o token de acesso (JWT).")
    public ResponseEntity<TokenResponseDTO> verify2FA(@Valid @RequestBody Verify2FARequestDTO verifyRequest) {
        String token = authService.verify2FAAndGenerateToken(verifyRequest);
        
        return ResponseEntity.ok(new TokenResponseDTO(token));
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String token){
        authService.logout(token);
        return ResponseEntity.ok("Logout realizado com sucesso");
    }

    // POST /auth/forgot-password
    @PostMapping("/forgot-password")
    public ResponseEntity<String> forgotPassword(@RequestParam String email) {
        authService.createPasswordResetTokenForUser(email);
        return ResponseEntity.ok("Se o e-mail existir, um link de recuperação foi enviado.");
    }

    // POST /auth/reset-password
    @PostMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestParam String token, @RequestParam String newPassword) {
        authService.resetPassword(token, newPassword);
        return ResponseEntity.ok("Senha redefinida com sucesso.");
    }
}