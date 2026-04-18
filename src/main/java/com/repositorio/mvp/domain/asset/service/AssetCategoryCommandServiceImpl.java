package com.repositorio.mvp.domain.asset.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.repositorio.mvp.common.constants.MessageConstants;
import com.repositorio.mvp.domain.asset.DTO.CategoryRequestDTO;
import com.repositorio.mvp.domain.asset.DTO.CategoryResponseDTO;
import com.repositorio.mvp.domain.asset.mapper.AssetMapper;
import com.repositorio.mvp.domain.asset.model.AssetCategory;
import com.repositorio.mvp.domain.asset.repository.AssetCategoryRepository;
import com.repositorio.mvp.domain.asset.service.interfaces.AssetCategoryCommandService;
import com.repositorio.mvp.domain.portfolio.model.Portfolio;
import com.repositorio.mvp.infrastructure.security.UserContextService;

import jakarta.persistence.EntityNotFoundException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.repositorio.mvp.common.constants.LogMessageConstants;

@Slf4j
@Service
@RequiredArgsConstructor
public class AssetCategoryCommandServiceImpl implements AssetCategoryCommandService {

    private final AssetCategoryRepository categoryRepository;
    private final UserContextService userContextService;
    private final AssetMapper assetMapper;

    @Override
    @Transactional
    public CategoryResponseDTO createCategory(@NonNull CategoryRequestDTO request) {
        Portfolio portfolio = userContextService.getCurrentUserPortfolio();
        portfolio.validateAndAddCategoryTarget(request.targetPercentage(), null);

        AssetCategory category = assetMapper.toEntity(request);
        category.setPortfolio(portfolio);
        
        AssetCategory savedCategory = categoryRepository.save(category);
        log.info(LogMessageConstants.AUDIT.CATEGORY_CREATED, savedCategory.getId(), savedCategory.getName());
        return assetMapper.toResponse(savedCategory);
    }

    @Override
    @Transactional
    public CategoryResponseDTO updateCategory(@NonNull UUID id, @NonNull CategoryRequestDTO request) {
        Portfolio portfolio = userContextService.getCurrentUserPortfolio();
        AssetCategory category = categoryRepository.findByIdAndPortfolioId(id, portfolio.getId())
            .orElseThrow(() -> new EntityNotFoundException(MessageConstants.Asset.CATEGORY_NOT_FOUND));

        portfolio.validateAndAddCategoryTarget(request.targetPercentage(), id);
        category.setName(request.name());
        category.setTargetPercentage(request.targetPercentage());

        AssetCategory updatedCategory = categoryRepository.save(category);
        log.info(LogMessageConstants.AUDIT.CATEGORY_UPDATED, updatedCategory.getId(), updatedCategory.getName());
        return assetMapper.toResponse(updatedCategory);
    }

    @Override
    @Transactional
    public void deleteCategory(@NonNull UUID id) {
        Portfolio portfolio = userContextService.getCurrentUserPortfolio();
        AssetCategory category = categoryRepository.findByIdAndPortfolioId(id, portfolio.getId())
            .orElseThrow(() -> new EntityNotFoundException(MessageConstants.Asset.CATEGORY_NOT_FOUND));
        
        categoryRepository.delete(category);
        log.info(LogMessageConstants.AUDIT.CATEGORY_DELETED, id);
    }
}
