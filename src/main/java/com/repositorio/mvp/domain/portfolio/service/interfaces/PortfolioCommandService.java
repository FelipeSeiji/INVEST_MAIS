package com.repositorio.mvp.domain.portfolio.service.interfaces;

import java.util.UUID;

import com.repositorio.mvp.common.result.ServiceResult;

import lombok.NonNull;

public interface PortfolioCommandService {
    ServiceResult<Void> createPortfolioForUser(@NonNull UUID userId);
}
