package com.repositorio.mvp.infrastructure.security;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.repositorio.mvp.common.constants.MessageConstants;
import com.repositorio.mvp.domain.portfolio.model.Portfolio;
import com.repositorio.mvp.domain.portfolio.repository.PortfolioRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserContextService {

    private final PortfolioRepository portfolioRepository;

    /**
     * Retorna o usuário logado com base no token JWT / Contexto do Spring Security.
     */
    public UserDetailsImpl getCurrentUserDetails() {
        return (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    /**
     * Retorna a carteira (Portfolio) do respectivo usuário logado.
     */
    public Portfolio getCurrentUserPortfolio() {
        UserDetailsImpl userDetails = getCurrentUserDetails();
        return portfolioRepository.findByUserId(userDetails.getUser().getId())
                .orElseThrow(() -> new EntityNotFoundException(MessageConstants.Portfolio.NOT_FOUND));
    }
    
    /**
     * Retorna a carteira carregada juntamente com as categorias e ativos.
     */
    public Portfolio getCurrentUserPortfolioWithCategoriesAndAssets() {
        UserDetailsImpl userDetails = getCurrentUserDetails();
        return portfolioRepository.findWithCategoriesAndAssetsByUserId(userDetails.getUser().getId())
                .orElseThrow(() -> new EntityNotFoundException(MessageConstants.Portfolio.NOT_FOUND));
    }
}
