package com.repositorio.mvp.domain.question.service;

import java.util.List;
import java.util.UUID;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.repositorio.mvp.common.constants.MessageConstants;
import com.repositorio.mvp.domain.asset.model.AssetCategory;
import com.repositorio.mvp.domain.asset.repository.AssetCategoryRepository;
import com.repositorio.mvp.domain.portfolio.model.Portfolio;
import com.repositorio.mvp.domain.portfolio.repository.PortfolioRepository;
import com.repositorio.mvp.domain.question.DTO.QuestionResponseDTO;
import com.repositorio.mvp.domain.question.mapper.QuestionMapper;
import com.repositorio.mvp.domain.question.repository.QuestionRepository;
import com.repositorio.mvp.domain.question.service.interfaces.QuestionQueryService;
import com.repositorio.mvp.infrastructure.security.UserDetailsImpl;

import jakarta.persistence.EntityNotFoundException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QuestionQueryServiceImpl implements QuestionQueryService {

    private final QuestionRepository questionRepository;
    private final AssetCategoryRepository categoryRepository;
    private final PortfolioRepository portfolioRepository;
    private final QuestionMapper questionMapper;

    @Override
    @Transactional(readOnly = true)
    public List<QuestionResponseDTO> listByCategoryId(@NonNull UUID categoryId){
        getCategoryForCurrentUser(categoryId); 
        return questionRepository.findAllByAssetCategoryId(categoryId).stream()
                .map(questionMapper::toResponse)
                .toList();
    }

    private AssetCategory getCategoryForCurrentUser(UUID categoryId) {
        Portfolio portfolio = getCurrentUserPortfolio();
        return categoryRepository.findByIdAndPortfolioId(categoryId, portfolio.getId())
                .orElseThrow(() -> new EntityNotFoundException(MessageConstants.Asset.CATEGORY_NOT_FOUND));
    }

    private Portfolio getCurrentUserPortfolio() {
        UserDetailsImpl userDetails = (UserDetailsImpl) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return portfolioRepository.findByUserId(userDetails.getUser().getId())
                .orElseThrow(() -> new EntityNotFoundException(MessageConstants.Portfolio.NOT_FOUND));
    }
}
