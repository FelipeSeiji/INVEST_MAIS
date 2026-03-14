package com.repositorio.mvp.controller.auth;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.repositorio.mvp.DTO.auth.AuthenticationDTO;
import com.repositorio.mvp.DTO.auth.LoginResponseDTO;
import com.repositorio.mvp.DTO.register.RegisterDTO;
import com.repositorio.mvp.service.auth.AuthFacadeService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthFacadeService authFacadeService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody @Valid AuthenticationDTO data, HttpServletRequest request) {
        String ip = request.getRemoteAddr();
        LoginResponseDTO token = authFacadeService.authenticate(data, ip);
        return ResponseEntity.ok(token);
    }

    @PostMapping("/register")
    public ResponseEntity<Void> register(@RequestBody @Valid RegisterDTO data) {
        authFacadeService.register(data);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}