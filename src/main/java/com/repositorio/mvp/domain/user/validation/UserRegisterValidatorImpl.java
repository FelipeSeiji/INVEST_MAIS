package com.repositorio.mvp.domain.user.validation;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Component;

import com.repositorio.mvp.domain.user.DTO.UserRequestDTO;
import com.repositorio.mvp.domain.user.repository.UserRepository;
import com.repositorio.mvp.domain.user.validation.interfaces.UserRegisterValidator;

@Component
public class UserRegisterValidatorImpl implements UserRegisterValidator {

    private final UserRepository userRepository;

    public UserRegisterValidatorImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void validate(UserRequestDTO request) {
        String hash = DigestUtils.sha256Hex(request.email().toLowerCase());
        
        if (userRepository.existsByEmailHash(hash)) {
            throw new IllegalArgumentException("Email já está em uso");
        }
    }
}