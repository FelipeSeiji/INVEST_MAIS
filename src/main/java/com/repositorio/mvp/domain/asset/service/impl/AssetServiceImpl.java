package com.repositorio.mvp.domain.asset.service.impl;

import java.util.List;
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
import com.repositorio.mvp.domain.asset.service.AssetService;
import com.repositorio.mvp.domain.portfolio.model.Portfolio;
import com.repositorio.mvp.infrastructure.security.UserContextService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AssetServiceImpl implements AssetService {

    private final AssetRepository assetRepository;
    private final AssetCategoryRepository categoryRepository;
    private final UserContextService userContextService;
    private final AssetMapper assetMapper;

    @Override
    @Transactional
    public AssetResponseDTO createAsset(UUID categoryId, AssetRequestDTO request) {
        AssetCategory category = getCategoryForCurrentUser(categoryId);
        
        Asset asset = assetMapper.toEntity(request);
        asset.setCategory(category);
        asset.setCurrentPositionValue(request.currentPositionValue());
        asset.setQuantity(request.quantity());
        asset.setAveragePrice(request.averagePrice());
        
        return assetMapper.toResponse(assetRepository.save(asset));
    }

    @Override
    @Transactional(readOnly = true)
    public List<AssetResponseDTO> listAssetsByCategory(UUID categoryId) {
        getCategoryForCurrentUser(categoryId);
        return assetRepository.findAllByCategoryId(categoryId).stream()
            .map(assetMapper::toResponse)
            .toList();
    }

    @Override
    @Transactional
    public AssetResponseDTO updateAsset(UUID id, AssetRequestDTO request) {
        Asset asset = assetRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(MessageConstants.Asset.NOT_FOUND));
        
        getCategoryForCurrentUser(asset.getCategory().getId()); // Valida posse

        asset.setTicker(request.ticker());
        asset.setCurrentPositionValue(request.currentPositionValue());
        asset.setQuantity(request.quantity());
        asset.setAveragePrice(request.averagePrice());

        return assetMapper.toResponse(assetRepository.save(asset));
    }

    @Override
    @Transactional
    public void deleteAsset(UUID id) {
        Asset asset = assetRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(MessageConstants.Asset.NOT_FOUND));
        
        getCategoryForCurrentUser(asset.getCategory().getId()); // Valida posse
        
        assetRepository.delete(asset);
    }

    private AssetCategory getCategoryForCurrentUser(UUID categoryId) {
        Portfolio portfolio = userContextService.getCurrentUserPortfolio();
        return categoryRepository.findByIdAndPortfolioId(categoryId, portfolio.getId())
                .orElseThrow(() -> new EntityNotFoundException(MessageConstants.Asset.CATEGORY_NOT_FOUND));
    }
}
