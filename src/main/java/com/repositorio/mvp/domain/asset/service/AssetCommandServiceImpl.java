package com.repositorio.mvp.domain.asset.service;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.repositorio.mvp.common.constants.LogMessageConstants;
import com.repositorio.mvp.common.constants.MessageConstants;
import com.repositorio.mvp.common.result.ServiceResult;
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

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementação do serviço de comandos para ativos (Assets).
 * Gerencia a criação, atualização e exclusão de ativos na carteira do usuário,
 * garantindo que as operações sejam restritas ao contexto do usuário autenticado.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AssetCommandServiceImpl implements AssetCommandService {

    private final AssetRepository assetRepository;
    private final AssetCategoryRepository categoryRepository;
    private final UserContextService userContextService;
    private final AssetMapper assetMapper;

    /**
     * {@inheritDoc}
     * Registra um novo ativo em uma categoria de investimento específica.
     * Garante que a categoria informada pertença ao usuário autenticado antes de criar o ativo.
     * 
     * @param categoryId UUID da categoria de destino.
     * @param request DTO com os dados do ativo (ticker, quantidade, preço médio, valor atual).
     * @return ServiceResult com o DTO do ativo criado ou erro caso a categoria não exista.
     */
    @Override
    @Transactional
    public ServiceResult<AssetResponseDTO> createAsset(@NonNull UUID categoryId, @NonNull AssetRequestDTO request) {
        return getCategoryForCurrentUser(categoryId)
            .map(category -> {
                Asset asset = assetMapper.toEntity(request);
                asset.setCategory(category);
                
                Asset savedAsset = assetRepository.save(asset);
                log.info(LogMessageConstants.AUDIT.ASSET_CREATED, 
                    savedAsset.getId(), 
                    savedAsset.getTicker(),
                    category.getName());
                return ServiceResult.success(assetMapper.toResponse(savedAsset));
            })
            .orElseGet(() -> ServiceResult.notFound(MessageConstants.Asset.CATEGORY_NOT_FOUND));
    }

    /**
     * {@inheritDoc}
     * Atualiza os dados de um ativo existente.
     * Valida a propriedade da categoria do ativo antes de permitir a atualização.
     * 
     * @param id UUID do ativo a ser atualizado.
     * @param request Novos dados do ativo.
     * @return ServiceResult com o ativo atualizado ou erro caso não seja encontrado.
     */
    @Override
    @Transactional
    public ServiceResult<AssetResponseDTO> updateAsset(@NonNull UUID id, @NonNull AssetRequestDTO request) {
        return assetRepository.findById(id)
            .map(asset -> {
                // Verifica permissão/existência da categoria
                if (getCategoryForCurrentUser(asset.getCategory().getId()).isEmpty()) {
                    return ServiceResult.<AssetResponseDTO>notFound(MessageConstants.Asset.CATEGORY_NOT_FOUND);
                }

                assetMapper.updateEntity(request, asset);

                Asset updatedAsset = assetRepository.save(asset);
                log.info(LogMessageConstants.AUDIT.ASSET_UPDATED, updatedAsset.getId(), updatedAsset.getTicker());
                return ServiceResult.success(assetMapper.toResponse(updatedAsset));
            })
            .orElseGet(() -> ServiceResult.notFound(MessageConstants.Asset.NOT_FOUND));
    }

    /**
     * {@inheritDoc}
     * Remove um ativo da base de dados.
     * Verifica se o usuário tem permissão para excluir o ativo com base na categoria vinculada.
     * 
     * @param id UUID do ativo a ser removido.
     * @return ServiceResult indicando sucesso ou erro caso o ativo não seja encontrado.
     */
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

    private Optional<AssetCategory> getCategoryForCurrentUser(UUID categoryId) {
        Portfolio portfolio = userContextService.getCurrentUserPortfolio();
        return categoryRepository.findByIdAndPortfolioId(categoryId, portfolio.getId());
    }
}
