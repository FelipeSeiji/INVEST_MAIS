package com.repositorio.mvp.domain.asset.service;

import java.util.List;
import java.util.UUID;

import com.repositorio.mvp.domain.asset.DTO.CategoryRequest;
import com.repositorio.mvp.domain.asset.DTO.CategoryResponse;

public interface AssetCategoryService {
    CategoryResponse createCategory(CategoryRequest request);
    List<CategoryResponse> listUserCategories();
    CategoryResponse updateCategory(UUID id, CategoryRequest request);
    void deleteCategory(UUID id);
}
