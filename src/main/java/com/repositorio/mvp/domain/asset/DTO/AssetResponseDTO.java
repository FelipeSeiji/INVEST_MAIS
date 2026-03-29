package com.repositorio.mvp.domain.asset.DTO;

import java.util.UUID;

import com.repositorio.mvp.domain.asset.model.enums.AssetCategory;

public record AssetResponseDTO(
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