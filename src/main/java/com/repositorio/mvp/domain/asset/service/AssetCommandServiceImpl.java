package com.repositorio.mvp.domain.asset.service;

import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.repositorio.mvp.common.constants.MessageConstants;
import com.repositorio.mvp.domain.asset.DTO.AssetRequestDTO;
import com.repositorio.mvp.domain.asset.DTO.AssetResponseDTO;
import com.repositorio.mvp.domain.asset.mapper.AssetMapper;
import com.repositorio.mvp.domain.asset.model.Asset;
import com.repositorio.mvp.domain.asset.model.AssetCategory;
import com.repositorio.mvp.domain.asset.repository.AssetCategoryRepository;
import com.repositorio.mvp.domain.asset.repository.AssetRepository;
import com.repositorio.mvp.domain.asset.service.interfaces.AssetCommandService;
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
public class AssetCommandServiceImpl implements AssetCommandService {

    private final AssetRepository assetRepository;
    private final AssetCategoryRepository categoryRepository;
    private final UserContextService userContextService;
    private final AssetMapper assetMapper;

    @Override
    @Transactional
    public AssetResponseDTO createAsset(@NonNull UUID categoryId, @NonNull AssetRequestDTO request) {
        AssetCategory category = getCategoryForCurrentUser(categoryId);
        
        Asset asset = assetMapper.toEntity(request);
        asset.setCategory(category);
        asset.setCurrentPositionValue(request.currentPositionValue());
        asset.setQuantity(request.quantity());
        asset.setAveragePrice(request.averagePrice());
        
        Asset savedAsset = assetRepository.save(asset);
        log.info(LogMessageConstants.AUDIT.ASSET_CREATED, 
            savedAsset.getId(), 
            savedAsset.getTicker(),
            category.getName());
        return assetMapper.toResponse(savedAsset);
    }

    @Override
    @Transactional
    public AssetResponseDTO updateAsset(@NonNull UUID id, @NonNull AssetRequestDTO request) {
        Asset asset = assetRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(MessageConstants.Asset.NOT_FOUND));
        
        getCategoryForCurrentUser(asset.getCategory().getId());

        asset.setTicker(request.ticker());
        asset.setCurrentPositionValue(request.currentPositionValue());
        asset.setQuantity(request.quantity());
        asset.setAveragePrice(request.averagePrice());

        Asset updatedAsset = assetRepository.save(asset);
        log.info(LogMessageConstants.AUDIT.ASSET_UPDATED, updatedAsset.getId(), updatedAsset.getTicker());
        return assetMapper.toResponse(updatedAsset);
    }

    @Override
    @Transactional
    public void deleteAsset(@NonNull UUID id) {
        Asset asset = assetRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(MessageConstants.Asset.NOT_FOUND));
        
        getCategoryForCurrentUser(asset.getCategory().getId());
        
        assetRepository.delete(asset);
        log.info(LogMessageConstants.AUDIT.ASSET_DELETED, id);
    }

    private AssetCategory getCategoryForCurrentUser(UUID categoryId) {
        Portfolio portfolio = userContextService.getCurrentUserPortfolio();
        return categoryRepository.findByIdAndPortfolioId(categoryId, portfolio.getId())
                .orElseThrow(() -> new EntityNotFoundException(MessageConstants.Asset.CATEGORY_NOT_FOUND));
    }
}
