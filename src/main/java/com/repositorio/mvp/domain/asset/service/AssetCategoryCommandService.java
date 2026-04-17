package com.repositorio.mvp.domain.asset.service;

import java.util.UUID;
import com.repositorio.mvp.domain.asset.DTO.CategoryRequestDTO;
import com.repositorio.mvp.domain.asset.DTO.CategoryResponseDTO;

public interface AssetCategoryCommandService {
    CategoryResponseDTO createCategory(CategoryRequestDTO request);
    CategoryResponseDTO updateCategory(UUID id, CategoryRequestDTO request);
    void deleteCategory(UUID id);
}
