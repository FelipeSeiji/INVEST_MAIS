package com.repositorio.mvp.service.validation;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.repositorio.mvp.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserValidation {
    private final UserRepository userRepository;

    public void validadeNewEmail(String email){
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email já está em uso");
        }
    }

    public void validadeUpdateEmail(String email, String currentEmail) {
        if (userRepository.existsByEmail(email) && !currentEmail.equals(email)){
            throw new IllegalArgumentException("Email já está em uso por outro usuário.");
        }
    }
}
