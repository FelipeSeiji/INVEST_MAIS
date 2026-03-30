package com.repositorio.mvp.domain.user.validation.interfaces;

import com.repositorio.mvp.domain.user.DTO.UserRequestDTO;

public interface UserRegisterValidator {
    void validate(UserRequestDTO request);
}
