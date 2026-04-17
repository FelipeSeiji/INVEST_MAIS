package com.repositorio.mvp.domain.portfolio.service;

import java.math.BigDecimal;
import java.util.UUID;

import com.repositorio.mvp.domain.portfolio.DTO.RebalanceResponseDTO;

public interface PortfolioService {
    void createPortfolioForUser(UUID userId);
    RebalanceResponseDTO calculateRebalance(BigDecimal aporteAmount);
    com.repositorio.mvp.domain.portfolio.DTO.DashboardResponseDTO getPortfolioSummary();
}
