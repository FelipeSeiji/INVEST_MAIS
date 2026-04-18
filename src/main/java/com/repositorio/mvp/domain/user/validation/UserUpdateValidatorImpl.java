package com.repositorio.mvp.domain.user.validation;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Component;

import com.repositorio.mvp.common.constants.MessageConstants;
import com.repositorio.mvp.domain.user.DTO.UserUpdateRequestDTO;
import com.repositorio.mvp.domain.user.model.User;
import com.repositorio.mvp.domain.user.repository.UserRepository;
import com.repositorio.mvp.domain.user.validation.interfaces.UserUpdateValidator;
import lombok.NonNull;

@Component
public class UserUpdateValidatorImpl implements UserUpdateValidator {

    private final UserRepository userRepository;

    public UserUpdateValidatorImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void validate(@NonNull UserUpdateRequestDTO request, @NonNull User user) {
        if (request.email().equalsIgnoreCase(user.getEmail())) {
            return;
        }

        String hash = DigestUtils.sha256Hex(request.email().toLowerCase());
        
        if (userRepository.existsBySecurityEmailHash(hash)) {
            throw new IllegalArgumentException(MessageConstants.User.EMAIL_ALREADY_IN_USE_BY_OTHER);
        }
    }
}