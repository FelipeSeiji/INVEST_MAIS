package com.repositorio.mvp.domain.asset.service;

import java.util.List;
import java.util.UUID;

import com.repositorio.mvp.domain.asset.DTO.AssetRequestDTO;
import com.repositorio.mvp.domain.asset.DTO.AssetResponseDTO;

public interface AssetService {
    AssetResponseDTO createAsset(UUID categoryId, AssetRequestDTO request);
    List<AssetResponseDTO> listAssetsByCategory(UUID categoryId);
    AssetResponseDTO updateAsset(UUID id, AssetRequestDTO request);
    void deleteAsset(UUID id);
}
