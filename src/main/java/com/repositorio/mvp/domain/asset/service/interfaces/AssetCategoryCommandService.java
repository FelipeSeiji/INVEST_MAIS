package com.repositorio.mvp.domain.asset.service.interfaces;

import java.util.UUID;

import com.repositorio.mvp.common.result.ServiceResult;
import com.repositorio.mvp.domain.asset.DTO.CategoryRequestDTO;
import com.repositorio.mvp.domain.asset.DTO.CategoryResponseDTO;

import lombok.NonNull;

public interface AssetCategoryCommandService {
    ServiceResult<CategoryResponseDTO> createCategory(@NonNull CategoryRequestDTO request);
    ServiceResult<CategoryResponseDTO> updateCategory(@NonNull UUID id, @NonNull CategoryRequestDTO request);
    ServiceResult<Void> deleteCategory(@NonNull UUID id);
}
