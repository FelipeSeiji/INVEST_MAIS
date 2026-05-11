package com.repositorio.mvp.domain.auth.token.DTO;

import jakarta.validation.constraints.NotBlank;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Resposta de token")
public record TokenResponseDTO(
    @Schema(description = "Token JWT", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    @NotBlank(message = "O token é obrigatório.")
    String token
) {}