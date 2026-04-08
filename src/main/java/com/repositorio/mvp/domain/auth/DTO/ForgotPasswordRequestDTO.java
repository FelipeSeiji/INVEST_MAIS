package com.repositorio.mvp.domain.auth.DTO;

import com.repositorio.mvp.common.validation.ValidEmail;

public record ForgotPasswordRequestDTO(
    @ValidEmail
    String email
) {}
