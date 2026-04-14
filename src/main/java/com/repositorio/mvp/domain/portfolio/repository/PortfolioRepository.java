package com.repositorio.mvp.domain.portfolio.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.repositorio.mvp.domain.portfolio.model.Portfolio;

@Repository
public interface PortfolioRepository extends JpaRepository<Portfolio, UUID> {
    Optional<Portfolio> findByUserId(UUID userId);

    // Carrega a árvore completa (Carteira -> Categorias -> Ativos) em uma única query otimizada
    @EntityGraph(attributePaths = {"categories", "categories.assets"})
    Optional<Portfolio> findWithCategoriesAndAssetsByUserId(UUID userId);
}