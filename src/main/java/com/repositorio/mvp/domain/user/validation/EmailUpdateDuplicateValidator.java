package com.repositorio.mvp.domain.user.validation;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Component;

import com.repositorio.mvp.common.constants.MessageConstants;
import com.repositorio.mvp.domain.user.DTO.UserUpdateRequestDTO;
import com.repositorio.mvp.domain.user.model.User;
import com.repositorio.mvp.domain.user.repository.UserRepository;
import com.repositorio.mvp.domain.user.validation.interfaces.UserUpdateValidator;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class EmailUpdateDuplicateValidator implements UserUpdateValidator {
    private final UserRepository userRepository;

    @Override
    public void validate(UserUpdateRequestDTO request, User user) {
        String hash = DigestUtils.sha256Hex(request.email().toLowerCase());
        if (userRepository.existsBySecurityEmailHash(hash) && !user.getEmail().equals(request.email())) {
            throw new IllegalArgumentException(MessageConstants.User.EMAIL_ALREADY_IN_USE_BY_OTHER);
        }
    }
}
