package com.repositorio.mvp.domain.asset.service.impl;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.repositorio.mvp.common.constants.MessageConstants;
import com.repositorio.mvp.domain.asset.DTO.CategoryRequestDTO;
import com.repositorio.mvp.domain.asset.DTO.CategoryResponseDTO;
import com.repositorio.mvp.domain.asset.mapper.AssetMapper;
import com.repositorio.mvp.domain.asset.model.AssetCategory;
import com.repositorio.mvp.domain.asset.repository.AssetCategoryRepository;
import com.repositorio.mvp.domain.asset.service.AssetCategoryCommandService;
import com.repositorio.mvp.domain.portfolio.model.Portfolio;
import com.repositorio.mvp.infrastructure.security.UserContextService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AssetCategoryCommandServiceImpl implements AssetCategoryCommandService {

    private final AssetCategoryRepository categoryRepository;
    private final UserContextService userContextService;
    private final AssetMapper assetMapper;

    @Override
    @Transactional
    public CategoryResponseDTO createCategory(CategoryRequestDTO request) {
        Portfolio portfolio = userContextService.getCurrentUserPortfolio();
        portfolio.validateAndAddCategoryTarget(request.targetPercentage(), null);

        AssetCategory category = assetMapper.toEntity(request);
        category.setPortfolio(portfolio);
        
        return assetMapper.toResponse(categoryRepository.save(category));
    }

    @Override
    @Transactional
    public CategoryResponseDTO updateCategory(UUID id, CategoryRequestDTO request) {
        Portfolio portfolio = userContextService.getCurrentUserPortfolio();
        AssetCategory category = categoryRepository.findByIdAndPortfolioId(id, portfolio.getId())
            .orElseThrow(() -> new EntityNotFoundException(MessageConstants.Asset.CATEGORY_NOT_FOUND));

        portfolio.validateAndAddCategoryTarget(request.targetPercentage(), id);
        category.setName(request.name());
        category.setTargetPercentage(request.targetPercentage());

        return assetMapper.toResponse(categoryRepository.save(category));
    }

    @Override
    @Transactional
    public void deleteCategory(UUID id) {
        Portfolio portfolio = userContextService.getCurrentUserPortfolio();
        AssetCategory category = categoryRepository.findByIdAndPortfolioId(id, portfolio.getId())
            .orElseThrow(() -> new EntityNotFoundException(MessageConstants.Asset.CATEGORY_NOT_FOUND));
        
        categoryRepository.delete(category);
    }
}
