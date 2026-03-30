package com.repositorio.mvp.domain.user.validation.interfaces;

import com.repositorio.mvp.domain.user.DTO.UserUpdateRequestDTO;
import com.repositorio.mvp.domain.user.model.User;

public interface UserUpdateValidator {
    void validate(UserUpdateRequestDTO request, User user);
}
