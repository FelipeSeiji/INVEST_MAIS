package com.repositorio.mvp.controller;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.repositorio.mvp.DTO.auth.AuthenticationDTO;
import com.repositorio.mvp.DTO.auth.LoginResponseDTO;
import com.repositorio.mvp.DTO.register.RegisterDTO;
import com.repositorio.mvp.repository.UserRepository;
import com.repositorio.mvp.repository.InvalidatedTokenRepository;
import com.repositorio.mvp.service.LoginAttemptService;
import com.repositorio.mvp.service.TokenService;
import com.repositorio.mvp.model.InvalidatedToken;
import com.repositorio.mvp.model.User;
import com.repositorio.mvp.model.UserRole;

import org.springframework.web.bind.annotation.RequestBody;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UserRepository repository;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private InvalidatedTokenRepository invalidatedTokenRepository;

    @Autowired
    private LoginAttemptService loginAttemptService;

    //POST /api/auth/login
    @PostMapping("/login")
    public ResponseEntity login(@RequestBody @Valid AuthenticationDTO data,
                            HttpServletRequest request) {

    String ip = request.getRemoteAddr();
    int attempts = loginAttemptService.getAttempts(ip);

    int delay = Math.min(attempts, 6);

    if (loginAttemptService.isBlocked(ip)) {
        return ResponseEntity
                .status(429)
                .body("IP temporarily blocked due to too many failed attempts");
    }
    
    if(delay > 0){
        try {
            Thread.sleep(delay * 1000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    try {
            var usernamePassword = new UsernamePasswordAuthenticationToken(data.login(), data.password());
            var auth = this.authenticationManager.authenticate(usernamePassword);

            loginAttemptService.loginSucceeded(ip);

            User user = (User) auth.getPrincipal();
            var token = tokenService.generateToken(user.getId());

            return ResponseEntity.ok(new LoginResponseDTO(token));

        } catch (Exception e) {
            loginAttemptService.loginFailed(ip);
            return ResponseEntity.status(401).build();
        }
    }
    //POST /api/auth/register
    @PostMapping("/register")
    public ResponseEntity register(@RequestBody @Valid RegisterDTO data){
        if(this.repository.findByEmail(data.email()) != null) return ResponseEntity.badRequest().build();

        String encryptedPassword = passwordEncoder.encode(data.password());
        User newUser = new User(data.name(), data.email(), encryptedPassword, UserRole.USER);

        this.repository.save(newUser);

        return ResponseEntity.ok().build();
    }    
    //POST /api/auth/logout
    @PostMapping("/logout")
    public ResponseEntity logout(HttpServletRequest request){

        String authHeader = request.getHeader("Authorization");

        if(authHeader == null || !authHeader.startsWith("Bearer "))
            return ResponseEntity.badRequest().build();

        String token = authHeader.substring(7);

        Instant expiration = tokenService.getExpiration(token);

        invalidatedTokenRepository.save(
            new InvalidatedToken(token, expiration)
        );

        return ResponseEntity.ok().build();
    }   

}
