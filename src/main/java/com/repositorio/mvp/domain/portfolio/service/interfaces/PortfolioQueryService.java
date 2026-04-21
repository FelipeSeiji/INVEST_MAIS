package com.repositorio.mvp.domain.portfolio.service.interfaces;

import java.math.BigDecimal;

import com.repositorio.mvp.common.result.ServiceResult;
import com.repositorio.mvp.domain.portfolio.DTO.DashboardResponseDTO;
import com.repositorio.mvp.domain.portfolio.DTO.RebalanceResponseDTO;

import lombok.NonNull;

public interface PortfolioQueryService {
    ServiceResult<RebalanceResponseDTO> calculateRebalance(@NonNull BigDecimal aporteAmount);
    ServiceResult<DashboardResponseDTO> getPortfolioSummary();
}
