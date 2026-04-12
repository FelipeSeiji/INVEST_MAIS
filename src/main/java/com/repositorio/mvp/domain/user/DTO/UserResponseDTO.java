package com.repositorio.mvp.domain.user.DTO;

import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;

public record UserResponseDTO (
    @Schema(description = "ID do usuário", example = "123e4567-e89b-12d3-a456-426614174000")
    UUID id,
    @Schema(description = "Nome do usuário", example = "User Name")
    String name,
    @Schema(description = "E-mail do usuário", example = "example@gmail.com")
    String email
) {}
