package com.repositorio.mvp.controller.auth;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.repositorio.mvp.DTO.auth.LoginRequestDTO;
import com.repositorio.mvp.DTO.auth.TokenResponseDTO;
import com.repositorio.mvp.DTO.auth.Verify2FARequestDTO;
import com.repositorio.mvp.service.auth.AuthService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Autenticação", description = "Endpoints para login e verificação de 2 fatores (2FA)")
public class AuthController {

    private final AuthService authService;

    // POST /auth/login
    @PostMapping("/login")
    @Operation(summary = "Inicia o login e envia código 2FA", description = "Valida e-mail e senha. Se corretos, envia um e-mail com o código de 6 dígitos para o usuário.")
    public ResponseEntity<String> login(@Valid @RequestBody LoginRequestDTO loginRequest) {
        authService.initiateLogin(loginRequest);
        
        // Retornamos apenas uma mensagem de sucesso. O front-end saberá que deve abrir a tela de digitar o código.
        return ResponseEntity.ok("Código de verificação enviado para o seu e-mail.");
    }

    // POST /auth/verify-2fa
    @PostMapping("/verify-2fa")
    @Operation(summary = "Valida o código 2FA e retorna o JWT", description = "Valida o código recebido por e-mail. Se correto e no prazo, devolve o token de acesso (JWT).")
    public ResponseEntity<TokenResponseDTO> verify2FA(@Valid @RequestBody Verify2FARequestDTO verifyRequest) {
        String token = authService.verify2FAAndGenerateToken(verifyRequest);
        
        // Retorna o DTO imutável (record) contendo o token gerado
        return ResponseEntity.ok(new TokenResponseDTO(token));
    }
}