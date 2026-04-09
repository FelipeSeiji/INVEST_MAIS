package com.repositorio.mvp.domain.auth.service.login;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.repositorio.mvp.domain.auth.service.interfaces.CodeGenerator;
import com.repositorio.mvp.domain.user.model.User;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class TwoFactorService {
    private final CodeGenerator codeGenerator;
    
    public void prepareTwoFactor(User user) {
        String code = codeGenerator.generate(6);
        user.getSecurity().generateTwoFactorCode(
            code, 
            LocalDateTime.now()
                .plusMinutes(5)
        );
    }
}