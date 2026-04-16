package com.repositorio.mvp.domain.asset.service.impl;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.repositorio.mvp.common.constants.MessageConstants;
import com.repositorio.mvp.domain.asset.DTO.CategoryRequest;
import com.repositorio.mvp.domain.asset.DTO.CategoryResponse;
import com.repositorio.mvp.domain.asset.mapper.AssetMapper;
import com.repositorio.mvp.domain.asset.model.AssetCategory;
import com.repositorio.mvp.domain.asset.repository.AssetCategoryRepository;
import com.repositorio.mvp.domain.asset.service.AssetCategoryService;
import com.repositorio.mvp.domain.portfolio.model.Portfolio;
import com.repositorio.mvp.domain.portfolio.repository.PortfolioRepository;
import com.repositorio.mvp.infrastructure.security.UserDetailsImpl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AssetCategoryServiceImpl implements AssetCategoryService {

    private final AssetCategoryRepository categoryRepository;
    private final PortfolioRepository portfolioRepository;
    private final AssetMapper assetMapper;

    @Override
    @Transactional
    public CategoryResponse createCategory(CategoryRequest request) {
        Portfolio portfolio = getCurrentUserPortfolio();
        
        validateTargetPercentage(portfolio, request.targetPercentage(), null);

        AssetCategory category = assetMapper.toEntity(request);
        category.setPortfolio(portfolio);
        
        return assetMapper.toResponse(categoryRepository.save(category));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponse> listUserCategories() {
        Portfolio portfolio = getCurrentUserPortfolio();
        return categoryRepository.findAllByPortfolioId(portfolio.getId()).stream()
                .map(assetMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public CategoryResponse updateCategory(UUID id, CategoryRequest request) {
        Portfolio portfolio = getCurrentUserPortfolio();
        AssetCategory category = categoryRepository.findByIdAndPortfolioId(id, portfolio.getId())
                .orElseThrow(() -> new EntityNotFoundException(MessageConstants.Asset.CATEGORY_NOT_FOUND));

        validateTargetPercentage(portfolio, request.targetPercentage(), id);

        category.setName(request.name());
        category.setTargetPercentage(request.targetPercentage());

        return assetMapper.toResponse(categoryRepository.save(category));
    }

    @Override
    @Transactional
    public void deleteCategory(UUID id) {
        Portfolio portfolio = getCurrentUserPortfolio();
        AssetCategory category = categoryRepository.findByIdAndPortfolioId(id, portfolio.getId())
                .orElseThrow(() -> new EntityNotFoundException(MessageConstants.Asset.CATEGORY_NOT_FOUND));
        
        categoryRepository.delete(category);
    }

    private Portfolio getCurrentUserPortfolio() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return portfolioRepository.findByUserId(userDetails.getUser().getId())
                .orElseThrow(() -> new EntityNotFoundException(MessageConstants.Portfolio.NOT_FOUND));
    }

    private void validateTargetPercentage(Portfolio portfolio, BigDecimal newValue, UUID excludeId) {
        BigDecimal currentTotal = categoryRepository.findAllByPortfolioId(portfolio.getId()).stream()
                .filter(c -> excludeId == null || !c.getId().equals(excludeId))
                .map(AssetCategory::getTargetPercentage)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (currentTotal.add(newValue).compareTo(new BigDecimal("100")) > 0) {
            throw new IllegalArgumentException(MessageConstants.Asset.CATEGORY_TARGET_EXCEEDED);
        }
    }
}
