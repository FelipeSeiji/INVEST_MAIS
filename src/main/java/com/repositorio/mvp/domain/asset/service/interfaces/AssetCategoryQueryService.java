package com.repositorio.mvp.domain.asset.service.interfaces;

import java.util.List;

import com.repositorio.mvp.domain.asset.DTO.CategoryResponseDTO;

public interface AssetCategoryQueryService {
    List<CategoryResponseDTO> listUserCategories();
}
