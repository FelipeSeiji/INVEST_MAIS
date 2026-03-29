package com.repositorio.mvp.domain.mark.DTO;

import java.util.UUID;

import com.repositorio.mvp.domain.asset.model.enums.AssetCategory;

public record MarkResponseDTO(
    UUID id,
    Double percentage,
    String label,
    AssetCategory categoryActive
) {}
