package com.repositorio.mvp.domain.portfolio.controller;

import java.math.BigDecimal;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.repositorio.mvp.domain.portfolio.DTO.RebalanceResponseDTO;
import com.repositorio.mvp.domain.portfolio.service.PortfolioService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/portfolio")
@RequiredArgsConstructor
@Tag(name = "Portfolio & Rebalancing", description = "Endpoints para visualização da carteira e cálculo de rebalanceamento")
public class PortfolioController {

    private final PortfolioService portfolioService;

    @GetMapping("/rebalance")
    @Operation(summary = "Calcula o rebalanceamento da carteira", description = "Baseado no valor do aporte, sugere onde investir para atingir os alvos definidos.")
    public RebalanceResponseDTO rebalance(@RequestParam(defaultValue = "0") BigDecimal aporteAmount) {
        return portfolioService.calculateRebalance(aporteAmount);
    }

    @GetMapping("/summary")
    @Operation(summary = "Fornece o resumo estratégico da carteira", description = "Retorna o retrato estruturado: Valor Total, Alvos Proporcionais e Distribuição por Categoria.")
    public com.repositorio.mvp.domain.portfolio.DTO.DashboardResponseDTO getSummary() {
        return portfolioService.getPortfolioSummary();
    }
}
