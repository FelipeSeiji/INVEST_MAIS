package com.repositorio.mvp.domain.portfolio.service.impl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.repositorio.mvp.common.constants.MessageConstants;
import com.repositorio.mvp.domain.asset.engine.AssetScoreCalculator;
import com.repositorio.mvp.domain.asset.model.Asset;
import com.repositorio.mvp.domain.asset.model.AssetCategory;
import com.repositorio.mvp.domain.asset.repository.AssetCategoryRepository;
import com.repositorio.mvp.domain.portfolio.DTO.DashboardResponseDTO;
import com.repositorio.mvp.domain.portfolio.DTO.RebalanceResponseDTO;
import com.repositorio.mvp.domain.portfolio.engine.RebalanceEngine;
import com.repositorio.mvp.domain.portfolio.model.Portfolio;
import com.repositorio.mvp.domain.portfolio.repository.PortfolioRepository;
import com.repositorio.mvp.domain.portfolio.service.PortfolioService;
import com.repositorio.mvp.domain.user.model.User;
import com.repositorio.mvp.domain.user.repository.UserRepository;
import com.repositorio.mvp.infrastructure.security.UserContextService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PortfolioServiceImpl implements PortfolioService {

        private final PortfolioRepository portfolioRepository;
        private final UserRepository userRepository;
        private final AssetCategoryRepository categoryRepository;
        
        private final UserContextService userContextService;
        private final AssetScoreCalculator assetScoreCalculator;
        private final RebalanceEngine rebalanceEngine;

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
        public RebalanceResponseDTO calculateRebalance(BigDecimal aporteAmount) {
                Portfolio portfolio = userContextService.getCurrentUserPortfolioWithCategoriesAndAssets();

                return rebalanceEngine.calculate(portfolio, aporteAmount);
        }

        @Override
        @Transactional(readOnly = true)
        public com.repositorio.mvp.domain.portfolio.DTO.DashboardResponseDTO getPortfolioSummary() {
                Portfolio portfolio = userContextService.getCurrentUserPortfolioWithCategoriesAndAssets();

                BigDecimal totalValue = calculateTotalValue(portfolio);
                
                List<AssetCategory> activeCategories = portfolio.getCategories().stream()
                                .filter(c -> c.getAssets().stream().anyMatch(a -> assetScoreCalculator.calculateScore(a) > 0))
                                .toList();

                BigDecimal sumActiveOriginalTargets = activeCategories.stream()
                                .map(AssetCategory::getTargetPercentage)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                int totalAssets = (int) portfolio.getCategories().stream()
                                .flatMap(c -> c.getAssets().stream())
                                .count();

                List<DashboardResponseDTO.CategorySummaryDTO> summaries = new ArrayList<>();

                for (AssetCategory category : portfolio.getCategories()) {
                        BigDecimal currentCategoryValue = category.getAssets().stream()
                                        .map(Asset::getCurrentPositionValue)
                                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                        BigDecimal currentPercentage = totalValue.compareTo(BigDecimal.ZERO) > 0
                                        ? currentCategoryValue.multiply(new BigDecimal("100")).divide(totalValue, 2, RoundingMode.HALF_UP)
                                        : BigDecimal.ZERO;

                        BigDecimal redistributedTarget = BigDecimal.ZERO;
                        if (activeCategories.contains(category) && sumActiveOriginalTargets.compareTo(BigDecimal.ZERO) > 0) {
                                redistributedTarget = category.getTargetPercentage()
                                                .multiply(new BigDecimal("100"))
                                                .divide(sumActiveOriginalTargets, 2, RoundingMode.HALF_UP);
                        }

                        summaries.add(new DashboardResponseDTO.CategorySummaryDTO(
                                        category.getId(),
                                        category.getName(),
                                        currentPercentage,
                                        redistributedTarget,
                                        currentCategoryValue,
                                        category.getAssets().size()
                        ));
                }

                return new DashboardResponseDTO(totalValue, summaries, totalAssets);
        }

        private BigDecimal calculateTotalValue(Portfolio portfolio) {
                return portfolio.getCategories().stream()
                                .flatMap(c -> c.getAssets().stream())
                                .map(Asset::getCurrentPositionValue)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);
        }
}
