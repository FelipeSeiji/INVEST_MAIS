package com.repositorio.mvp.domain.portfolio.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.repositorio.mvp.common.constants.MessageConstants;
import com.repositorio.mvp.domain.asset.model.Asset;
import com.repositorio.mvp.domain.asset.model.AssetCategory;
import com.repositorio.mvp.domain.asset.model.AssetEvaluation;
import com.repositorio.mvp.domain.asset.repository.AssetCategoryRepository;
import com.repositorio.mvp.domain.asset.repository.AssetEvaluationRepository;
import com.repositorio.mvp.domain.asset.repository.AssetRepository;
import com.repositorio.mvp.domain.portfolio.DTO.RebalanceResponse;
import com.repositorio.mvp.domain.portfolio.model.Portfolio;
import com.repositorio.mvp.domain.portfolio.repository.PortfolioRepository;
import com.repositorio.mvp.domain.portfolio.service.PortfolioService;
import com.repositorio.mvp.domain.user.model.User;
import com.repositorio.mvp.domain.user.repository.UserRepository;
import com.repositorio.mvp.infrastructure.security.UserDetailsImpl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PortfolioServiceImpl implements PortfolioService {

        private final PortfolioRepository portfolioRepository;
        private final UserRepository userRepository;
        private final AssetCategoryRepository categoryRepository;

        @Override
        @Transactional
        public void createPortfolioForUser(UUID userId) {
                if (portfolioRepository.findByUserId(userId).isEmpty()) {
                        User user = userRepository.findById(userId)
                                        .orElseThrow(() -> new EntityNotFoundException(
                                                        MessageConstants.User.NOT_FOUND_WITH_ID + userId));

                        Portfolio portfolio = Portfolio.builder()
                                        .user(user)
                                        .build();
                        Portfolio savedPortfolio = portfolioRepository.save(portfolio);

                        // Ponto 1 do MVP: Pré-definição de Categorias Genéricas
                        createDefaultCategories(savedPortfolio);
                }
        }

        private void createDefaultCategories(Portfolio portfolio) {
                List<AssetCategory> defaults = List.of(
                                AssetCategory.builder().name("AÇÃO NACIONAL").targetPercentage(new BigDecimal("25"))
                                                .portfolio(portfolio).build(),
                                AssetCategory.builder().name("FUNDOS IMOBILIÁRIOS NACIONAL")
                                                .targetPercentage(new BigDecimal("15")).portfolio(portfolio).build(),
                                AssetCategory.builder().name("AÇÃO INTERNACIONAL").targetPercentage(new BigDecimal("20"))
                                                .portfolio(portfolio).build(),
                                AssetCategory.builder().name("RENDA FIXA NACIONAL").targetPercentage(new BigDecimal("25"))
                                                .portfolio(portfolio).build(),
                                AssetCategory.builder().name("CRIPTOMOEDA").targetPercentage(new BigDecimal("10"))
                                                .portfolio(portfolio).build(),
                                AssetCategory.builder().name("RENDA FIXA INTERNACIONAL")
                                                .targetPercentage(new BigDecimal("5")).portfolio(portfolio).build());
                categoryRepository.saveAll(defaults);
        }

        @Override
        @Transactional(readOnly = true)
        public RebalanceResponse calculateRebalance(BigDecimal aporteAmount) {
                UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication()
                                .getPrincipal();
                Portfolio portfolio = portfolioRepository
                                .findWithCategoriesAndAssetsByUserId(userDetails.getUser().getId())
                                .orElseThrow(() -> new EntityNotFoundException(MessageConstants.Portfolio.NOT_FOUND));

                BigDecimal totalCurrentValue = calculateTotalValue(portfolio);
                BigDecimal newTotalValue = totalCurrentValue.add(aporteAmount);

                // 1. Identificar categorias "vivas" (aquelas que possuem pelo menos um ativo aprovado com score > 0)
                List<AssetCategory> activeCategories = portfolio.getCategories().stream()
                                .filter(c -> c.getAssets().stream().anyMatch(a -> calculateAssetScore(a) > 0))
                                .toList();

                // 2. Calcular a soma dos alvos percentuais das categorias vivas para redistribuição (Auto-Cura)
                BigDecimal sumActiveOriginalTargets = activeCategories.stream()
                                .map(AssetCategory::getTargetPercentage)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                // PASSO 1: Calcular os GAPs brutos (necessidade) de todos os ativos e somá-los
                BigDecimal totalPositiveGap = BigDecimal.ZERO;
                java.util.Map<UUID, BigDecimal> rawGaps = new java.util.HashMap<>();
                java.util.Map<UUID, BigDecimal> redistributedTargets = new java.util.HashMap<>();

                for (AssetCategory category : portfolio.getCategories()) {
                        boolean isActive = activeCategories.contains(category);

                        // 3. Redistribuição Proporcional (Auto-Cura):
                        BigDecimal redistributedTargetPercentage = BigDecimal.ZERO;
                        if (isActive && sumActiveOriginalTargets.compareTo(BigDecimal.ZERO) > 0) {
                                redistributedTargetPercentage = category.getTargetPercentage()
                                                .multiply(new BigDecimal("100"))
                                                .divide(sumActiveOriginalTargets, 2, RoundingMode.HALF_UP);
                        }
                        redistributedTargets.put(category.getId(), redistributedTargetPercentage);

                        // Define o target financeiro da categoria nesta simulação
                        BigDecimal categoryTargetValue = newTotalValue.multiply(redistributedTargetPercentage)
                                        .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);

                        int totalCategoryScore = category.getAssets().stream()
                                        .map(this::calculateAssetScore)
                                        .filter(score -> score > 0)
                                        .mapToInt(Integer::intValue)
                                        .sum();

                        for (Asset asset : category.getAssets()) {
                                int score = calculateAssetScore(asset);

                                // Linha de Corte: Ativos com score <= 0 recebem peso zero
                                BigDecimal assetTargetValue = (isActive && score > 0 && totalCategoryScore > 0)
                                                ? categoryTargetValue.multiply(new BigDecimal(score)).divide(
                                                                new BigDecimal(totalCategoryScore), 2, RoundingMode.HALF_UP)
                                                : BigDecimal.ZERO;

                                BigDecimal gap = assetTargetValue.subtract(asset.getCurrentPositionValue());
                                if (gap.compareTo(BigDecimal.ZERO) < 0) {
                                        gap = BigDecimal.ZERO; // Regra de Hold: Não recomendamos vender
                                }

                                rawGaps.put(asset.getId(), gap);
                                totalPositiveGap = totalPositiveGap.add(gap);
                        }
                }

                // PASSO 2: Fatiar o Aporte e Montar a Resposta
                List<RebalanceResponse.CategoryRebalanceDTO> categoryDTOs = new ArrayList<>();

                for (AssetCategory category : portfolio.getCategories()) {
                        BigDecimal redistributedTargetPercentage = redistributedTargets.getOrDefault(category.getId(), BigDecimal.ZERO);

                        BigDecimal currentCategoryValue = category.getAssets().stream()
                                        .map(Asset::getCurrentPositionValue)
                                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                        BigDecimal categoryCurrentPercentage = totalCurrentValue.compareTo(BigDecimal.ZERO) > 0
                                        ? currentCategoryValue.multiply(new BigDecimal("100")).divide(totalCurrentValue, 2, RoundingMode.HALF_UP)
                                        : BigDecimal.ZERO;

                        List<RebalanceResponse.AssetRebalanceDTO> assetDTOs = new ArrayList<>();

                        for (Asset asset : category.getAssets()) {
                                BigDecimal assetCurrentPercentage = totalCurrentValue.compareTo(BigDecimal.ZERO) > 0
                                                ? asset.getCurrentPositionValue().multiply(new BigDecimal("100"))
                                                                .divide(totalCurrentValue, 2, RoundingMode.HALF_UP)
                                                : BigDecimal.ZERO;

                                BigDecimal rawGap = rawGaps.getOrDefault(asset.getId(), BigDecimal.ZERO);
                                BigDecimal suggestedAporte = BigDecimal.ZERO;

                                // Lógica de Fatiamento (Sardinha): Distribuímos os R$ do bolso baseado na proporção da necessidade!
                                if (totalPositiveGap.compareTo(BigDecimal.ZERO) > 0 && aporteAmount.compareTo(BigDecimal.ZERO) > 0) {
                                        suggestedAporte = rawGap.multiply(aporteAmount).divide(totalPositiveGap, 2, RoundingMode.HALF_UP);
                                }

                                assetDTOs.add(new RebalanceResponse.AssetRebalanceDTO(
                                                asset.getId(),
                                                asset.getTicker(),
                                                assetCurrentPercentage,
                                                BigDecimal.ZERO, // Target individual opcional
                                                suggestedAporte,
                                                suggestedAporte.compareTo(BigDecimal.ZERO) > 0 ? "COMPRAR" : "AGUARDAR"));
                        }

                        categoryDTOs.add(new RebalanceResponse.CategoryRebalanceDTO(
                                        category.getId(),
                                        category.getName(),
                                        categoryCurrentPercentage,
                                        redistributedTargetPercentage, // Exibe o alvo redistribuído (Auto-Cura aplicada)
                                        assetDTOs));
                }

                return new RebalanceResponse(totalCurrentValue, categoryDTOs);
        }

        @Override
        @Transactional(readOnly = true)
        public com.repositorio.mvp.domain.portfolio.DTO.DashboardResponse getPortfolioSummary() {
                UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication()
                                .getPrincipal();
                Portfolio portfolio = portfolioRepository
                                .findWithCategoriesAndAssetsByUserId(userDetails.getUser().getId())
                                .orElseThrow(() -> new EntityNotFoundException(MessageConstants.Portfolio.NOT_FOUND));

                BigDecimal totalValue = calculateTotalValue(portfolio);
                
                // 1. Identificar categorias "vivas" (aquelas que possuem pelo menos um ativo aprovado com score > 0)
                List<AssetCategory> activeCategories = portfolio.getCategories().stream()
                                .filter(c -> c.getAssets().stream().anyMatch(a -> calculateAssetScore(a) > 0))
                                .toList();

                // 2. Calcular a soma dos alvos percentuais das categorias vivas
                BigDecimal sumActiveOriginalTargets = activeCategories.stream()
                                .map(AssetCategory::getTargetPercentage)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                int totalAssets = (int) portfolio.getCategories().stream()
                                .flatMap(c -> c.getAssets().stream())
                                .count();

                List<com.repositorio.mvp.domain.portfolio.DTO.DashboardResponse.CategorySummaryDTO> summaries = new ArrayList<>();

                for (AssetCategory category : portfolio.getCategories()) {
                        BigDecimal currentCategoryValue = category.getAssets().stream()
                                        .map(Asset::getCurrentPositionValue)
                                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                        BigDecimal currentPercentage = totalValue.compareTo(BigDecimal.ZERO) > 0
                                        ? currentCategoryValue.multiply(new BigDecimal("100")).divide(totalValue, 2, RoundingMode.HALF_UP)
                                        : BigDecimal.ZERO;

                        // Auto-Cura para exibição no Dashboard
                        BigDecimal redistributedTarget = BigDecimal.ZERO;
                        if (activeCategories.contains(category) && sumActiveOriginalTargets.compareTo(BigDecimal.ZERO) > 0) {
                                redistributedTarget = category.getTargetPercentage()
                                                .multiply(new BigDecimal("100"))
                                                .divide(sumActiveOriginalTargets, 2, RoundingMode.HALF_UP);
                        }

                        summaries.add(new com.repositorio.mvp.domain.portfolio.DTO.DashboardResponse.CategorySummaryDTO(
                                        category.getId(),
                                        category.getName(),
                                        currentPercentage,
                                        redistributedTarget,
                                        currentCategoryValue,
                                        category.getAssets().size()
                        ));
                }

                return new com.repositorio.mvp.domain.portfolio.DTO.DashboardResponse(totalValue, summaries, totalAssets);
        }

        private BigDecimal calculateTotalValue(Portfolio portfolio) {
                return portfolio.getCategories().stream()
                                .flatMap(c -> c.getAssets().stream())
                                .map(Asset::getCurrentPositionValue)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);
        }

        /**
         * Sistema Quantitativo de Avaliação:
         * Resposta Positiva: +1 ponto
         * Resposta Negativa: -1 ponto
         */
        private int calculateAssetScore(Asset asset) {
                if (asset.getEvaluations() == null || asset.getEvaluations().isEmpty())
                        return 0; // Ativo sem notas começa com zero na nova lógica

                int positives = (int) asset.getEvaluations().stream().filter(AssetEvaluation::isPositive).count();
                int negatives = (int) asset.getEvaluations().stream().filter(e -> !e.isPositive()).count();

                return positives - negatives;
        }
}
