package com.repositorio.mvp.domain.user.validation;

import org.springframework.stereotype.Component;

import com.repositorio.mvp.common.constants.MessageConstants;
import com.repositorio.mvp.common.security.CryptoService;
import com.repositorio.mvp.domain.user.DTO.UserUpdateRequestDTO;
import com.repositorio.mvp.domain.user.model.User;
import com.repositorio.mvp.domain.user.repository.UserRepository;
import com.repositorio.mvp.domain.user.validation.interfaces.UserUpdateValidator;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserUpdateValidatorImpl implements UserUpdateValidator {

    private final UserRepository userRepository;
    private final CryptoService cryptoService;

    @Override
    public void validate(@NonNull UserUpdateRequestDTO request, @NonNull User user) {
        if (request.email().equalsIgnoreCase(user.getEmail())) {
            return;
        }

        String hash = cryptoService.generateSha256Hash(request.email());
        
        if (userRepository.existsBySecurityEmailHash(hash)) {
            throw new IllegalArgumentException(MessageConstants.User.EMAIL_ALREADY_IN_USE_BY_OTHER);
        }
    }
}