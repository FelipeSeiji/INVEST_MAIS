package com.repositorio.mvp.domain.asset.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.repositorio.mvp.common.constants.MessageConstants;
import com.repositorio.mvp.domain.asset.DTO.AssetResponseDTO;
import com.repositorio.mvp.domain.asset.mapper.AssetMapper;
import com.repositorio.mvp.domain.asset.repository.AssetCategoryRepository;
import com.repositorio.mvp.domain.asset.repository.AssetRepository;
import com.repositorio.mvp.domain.asset.service.interfaces.AssetQueryService;
import com.repositorio.mvp.domain.portfolio.model.Portfolio;
import com.repositorio.mvp.infrastructure.security.UserContextService;

import jakarta.persistence.EntityNotFoundException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AssetQueryServiceImpl implements AssetQueryService {

    private final AssetRepository assetRepository;
    private final AssetCategoryRepository categoryRepository;
    private final UserContextService userContextService;
    private final AssetMapper assetMapper;

    @Override
    @Transactional(readOnly = true)
    public List<AssetResponseDTO> listAssetsByCategory(@NonNull UUID categoryId) {
        getCategoryForCurrentUser(categoryId);
        return assetRepository.findAllByCategoryId(categoryId).stream()
            .map(assetMapper::toResponse)
            .toList();
    }

    private void getCategoryForCurrentUser(UUID categoryId) {
        Portfolio portfolio = userContextService.getCurrentUserPortfolio();
        categoryRepository.findByIdAndPortfolioId(categoryId, portfolio.getId())
            .orElseThrow(() -> new EntityNotFoundException(MessageConstants.Asset.CATEGORY_NOT_FOUND));
    }
}
