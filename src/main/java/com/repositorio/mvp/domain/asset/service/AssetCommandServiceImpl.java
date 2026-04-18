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
import com.repositorio.mvp.common.result.ServiceResult;

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
    public ServiceResult<AssetResponseDTO> createAsset(@NonNull UUID categoryId, @NonNull AssetRequestDTO request) {
        return getCategoryForCurrentUser(categoryId)
            .map(category -> {
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
                return ServiceResult.success(assetMapper.toResponse(savedAsset));
            })
            .orElseGet(() -> ServiceResult.notFound(MessageConstants.Asset.CATEGORY_NOT_FOUND));
    }

    @Override
    @Transactional
    public ServiceResult<AssetResponseDTO> updateAsset(@NonNull UUID id, @NonNull AssetRequestDTO request) {
        return assetRepository.findById(id)
            .map(asset -> {
                // Verifica permissão/existência da categoria
                if (getCategoryForCurrentUser(asset.getCategory().getId()).isEmpty()) {
                    return ServiceResult.<AssetResponseDTO>notFound(MessageConstants.Asset.CATEGORY_NOT_FOUND);
                }

                asset.setTicker(request.ticker());
                asset.setCurrentPositionValue(request.currentPositionValue());
                asset.setQuantity(request.quantity());
                asset.setAveragePrice(request.averagePrice());

                Asset updatedAsset = assetRepository.save(asset);
                log.info(LogMessageConstants.AUDIT.ASSET_UPDATED, updatedAsset.getId(), updatedAsset.getTicker());
                return ServiceResult.success(assetMapper.toResponse(updatedAsset));
            })
            .orElseGet(() -> ServiceResult.notFound(MessageConstants.Asset.NOT_FOUND));
    }

    @Override
    @Transactional
    public ServiceResult<Void> deleteAsset(@NonNull UUID id) {
        return assetRepository.findById(id)
            .map(asset -> {
                if (getCategoryForCurrentUser(asset.getCategory().getId()).isEmpty()) {
                    return ServiceResult.<Void>notFound(MessageConstants.Asset.CATEGORY_NOT_FOUND);
                }
                
                assetRepository.delete(asset);
                log.info(LogMessageConstants.AUDIT.ASSET_DELETED, id);
                return ServiceResult.<Void>success(null);
            })
            .orElseGet(() -> ServiceResult.notFound(MessageConstants.Asset.NOT_FOUND));
    }

    private java.util.Optional<AssetCategory> getCategoryForCurrentUser(UUID categoryId) {
        Portfolio portfolio = userContextService.getCurrentUserPortfolio();
        return categoryRepository.findByIdAndPortfolioId(categoryId, portfolio.getId());
    }
}
