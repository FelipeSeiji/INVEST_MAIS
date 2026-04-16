package com.repositorio.mvp.domain.portfolio.service;

import java.math.BigDecimal;
import java.util.UUID;

import com.repositorio.mvp.domain.portfolio.DTO.RebalanceResponse;

public interface PortfolioService {
    void createPortfolioForUser(UUID userId);
    RebalanceResponse calculateRebalance(BigDecimal aporteAmount);
    com.repositorio.mvp.domain.portfolio.DTO.DashboardResponse getPortfolioSummary();
}
