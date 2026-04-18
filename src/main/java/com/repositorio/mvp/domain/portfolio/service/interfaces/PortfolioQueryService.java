package com.repositorio.mvp.domain.portfolio.service.interfaces;

import java.math.BigDecimal;

import com.repositorio.mvp.domain.portfolio.DTO.DashboardResponseDTO;
import com.repositorio.mvp.domain.portfolio.DTO.RebalanceResponseDTO;

import lombok.NonNull;

public interface PortfolioQueryService {
    RebalanceResponseDTO calculateRebalance(@NonNull BigDecimal aporteAmount);
    DashboardResponseDTO getPortfolioSummary();
}
