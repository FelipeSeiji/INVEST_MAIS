package com.repositorio.mvp.domain.asset.service.interfaces;

import java.util.UUID;

import com.repositorio.mvp.domain.asset.DTO.AssetRequestDTO;
import com.repositorio.mvp.domain.asset.DTO.AssetResponseDTO;

import lombok.NonNull;

public interface AssetCommandService {
    AssetResponseDTO createAsset(@NonNull UUID categoryId, @NonNull AssetRequestDTO request);
    AssetResponseDTO updateAsset(@NonNull UUID id, @NonNull AssetRequestDTO request);
    void deleteAsset(@NonNull UUID id);
}
