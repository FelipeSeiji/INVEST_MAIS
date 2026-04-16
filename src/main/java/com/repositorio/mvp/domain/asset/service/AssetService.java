package com.repositorio.mvp.domain.asset.service;

import java.util.List;
import java.util.UUID;

import com.repositorio.mvp.domain.asset.DTO.AssetRequest;
import com.repositorio.mvp.domain.asset.DTO.AssetResponse;

public interface AssetService {
    AssetResponse createAsset(UUID categoryId, AssetRequest request);
    List<AssetResponse> listAssetsByCategory(UUID categoryId);
    AssetResponse updateAsset(UUID id, AssetRequest request);
    void deleteAsset(UUID id);
}
