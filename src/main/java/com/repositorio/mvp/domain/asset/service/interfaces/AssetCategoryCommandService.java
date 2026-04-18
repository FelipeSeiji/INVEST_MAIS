package com.repositorio.mvp.domain.asset.service.interfaces;

import java.util.UUID;

import com.repositorio.mvp.domain.asset.DTO.CategoryRequestDTO;
import com.repositorio.mvp.domain.asset.DTO.CategoryResponseDTO;

import lombok.NonNull;

public interface AssetCategoryCommandService {
    CategoryResponseDTO createCategory(@NonNull CategoryRequestDTO request);
    CategoryResponseDTO updateCategory(@NonNull UUID id, @NonNull CategoryRequestDTO request);
    void deleteCategory(@NonNull UUID id);
}
