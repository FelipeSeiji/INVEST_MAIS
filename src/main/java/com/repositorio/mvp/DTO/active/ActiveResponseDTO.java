package com.repositorio.mvp.DTO.active;

import java.util.UUID;
import com.repositorio.mvp.enums.AssetCategory;

public record ActiveResponseDTO(
    UUID id,
    String name,
    Integer amount,
    Double currentValue,
    Double note,
    Double price,
    Double recommend,
    Double percentage,
    UUID userId,
    AssetCategory categoryActive
) {}