package com.repositorio.mvp.domain.asset.service.interfaces;

import java.util.List;
import java.util.UUID;

import com.repositorio.mvp.common.result.ServiceResult;
import com.repositorio.mvp.domain.asset.DTO.AssetResponseDTO;

import lombok.NonNull;

public interface AssetQueryService {
    ServiceResult<List<AssetResponseDTO>> listAssetsByCategory(@NonNull UUID categoryId);
}
