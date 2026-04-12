package com.repositorio.mvp.domain.auth.DTO;

import io.swagger.v3.oas.annotations.media.Schema;

public record LoginResponseDTO(
    @Schema(description = "Token JWT", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    String token
) {}