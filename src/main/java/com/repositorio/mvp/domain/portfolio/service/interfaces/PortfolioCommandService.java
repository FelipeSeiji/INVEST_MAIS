package com.repositorio.mvp.domain.portfolio.service.interfaces;

import java.util.UUID;

import lombok.NonNull;

public interface PortfolioCommandService {
    void createPortfolioForUser(@NonNull UUID userId);
}
