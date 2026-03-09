package com.repositorio.mvp.DTO.mark;

import java.util.UUID;

import com.repositorio.mvp.enums.CategoryActive;

public record MarkResponseDTO(
    UUID id,
    Double percentage,
    String label,
    CategoryActive categoryActive
) {}
