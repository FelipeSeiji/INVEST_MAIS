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
import com.repositorio.mvp.common.result.ServiceResult;

@Slf4j
@Service
@RequiredArgsConstructor
public class AssetCategoryCommandServiceImpl implements AssetCategoryCommandService {

    private final AssetCategoryRepository categoryRepository;
    private final UserContextService userContextService;
    private final AssetMapper assetMapper;

    @Override
    @Transactional
    public ServiceResult<CategoryResponseDTO> createCategory(@NonNull CategoryRequestDTO request) {
        try {
            Portfolio portfolio = userContextService.getCurrentUserPortfolio();
            portfolio.validateAndAddCategoryTarget(request.targetPercentage(), null);

            AssetCategory category = assetMapper.toEntity(request);
            category.setPortfolio(portfolio);
            
            AssetCategory savedCategory = categoryRepository.save(category);
            log.info(LogMessageConstants.AUDIT.CATEGORY_CREATED, savedCategory.getId(), savedCategory.getName());
            return ServiceResult.success(assetMapper.toResponse(savedCategory));
        } catch (Exception e) {
            return ServiceResult.error(e.getMessage());
        }
    }

    @Override
    @Transactional
    public ServiceResult<CategoryResponseDTO> updateCategory(@NonNull UUID id, @NonNull CategoryRequestDTO request) {
        try {
            Portfolio portfolio = userContextService.getCurrentUserPortfolio();
            AssetCategory category = categoryRepository.findByIdAndPortfolioId(id, portfolio.getId())
                .orElseThrow(() -> new EntityNotFoundException(MessageConstants.Asset.CATEGORY_NOT_FOUND));

            portfolio.validateAndAddCategoryTarget(request.targetPercentage(), id);
            category.setName(request.name());
            category.setTargetPercentage(request.targetPercentage());

            AssetCategory updatedCategory = categoryRepository.save(category);
            log.info(LogMessageConstants.AUDIT.CATEGORY_UPDATED, updatedCategory.getId(), updatedCategory.getName());
            return ServiceResult.success(assetMapper.toResponse(updatedCategory));
        } catch (EntityNotFoundException e) {
            return ServiceResult.notFound(e.getMessage());
        } catch (Exception e) {
            return ServiceResult.error(e.getMessage());
        }
    }

    @Override
    @Transactional
    public ServiceResult<Void> deleteCategory(@NonNull UUID id) {
        Portfolio portfolio = userContextService.getCurrentUserPortfolio();
        return categoryRepository.findByIdAndPortfolioId(id, portfolio.getId())
            .map(category -> {
                categoryRepository.delete(category);
                log.info(LogMessageConstants.AUDIT.CATEGORY_DELETED, id);
                return ServiceResult.<Void>success(null);
            })
            .orElseGet(() -> ServiceResult.notFound(MessageConstants.Asset.CATEGORY_NOT_FOUND));
    }
}
