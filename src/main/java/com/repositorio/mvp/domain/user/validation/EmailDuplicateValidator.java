package com.repositorio.mvp.domain.user.validation;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Component;

import com.repositorio.mvp.domain.user.DTO.UserRequestDTO;
import com.repositorio.mvp.domain.user.repository.UserRepository;
import com.repositorio.mvp.domain.user.validation.interfaces.UserRegisterValidator;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class EmailDuplicateValidator implements UserRegisterValidator {
    private final UserRepository userRepository;

    @Override
    public void validate(@NonNull UserRequestDTO request) {
        String hash = DigestUtils.sha256Hex(request.email().toLowerCase());
        if (userRepository.existsBySecurityEmailHash(hash)) {
            throw new IllegalArgumentException("Email já está em uso");
        }
    }
}
