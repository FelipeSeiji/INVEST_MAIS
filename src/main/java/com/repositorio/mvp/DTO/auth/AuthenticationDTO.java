package com.repositorio.mvp.DTO.auth;

public record AuthenticationDTO(
    String login,
    String password,
    String totpCode
) {}