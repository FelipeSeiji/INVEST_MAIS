package com.repositorio.mvp.domain.user.validation;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Component;

import com.repositorio.mvp.domain.user.DTO.UserRequestDTO;
import com.repositorio.mvp.domain.user.repository.UserRepository;
import com.repositorio.mvp.domain.user.validation.interfaces.UserRegisterValidator;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class EmailDuplicateValidator implements UserRegisterValidator {
    private final UserRepository userRepository;

    @Override
    public void validate(UserRequestDTO request) {
        String hash = DigestUtils.sha256Hex(request.email().toLowerCase());
        if (userRepository.existsByEmailHash(hash)) {
            throw new IllegalArgumentException("Email já está em uso");
        }
    }
}
