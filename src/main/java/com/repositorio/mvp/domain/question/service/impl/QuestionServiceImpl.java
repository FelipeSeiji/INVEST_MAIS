package com.repositorio.mvp.domain.question.service.impl;

import java.util.List;
import java.util.UUID;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.repositorio.mvp.common.constants.MessageConstants;
import com.repositorio.mvp.domain.asset.model.Asset;
import com.repositorio.mvp.domain.asset.model.AssetCategory;
import com.repositorio.mvp.domain.asset.model.AssetEvaluation;
import com.repositorio.mvp.domain.asset.repository.AssetCategoryRepository;
import com.repositorio.mvp.domain.asset.repository.AssetEvaluationRepository;
import com.repositorio.mvp.domain.asset.repository.AssetRepository;
import com.repositorio.mvp.domain.portfolio.model.Portfolio;
import com.repositorio.mvp.domain.portfolio.repository.PortfolioRepository;
import com.repositorio.mvp.domain.question.DTO.EvaluationRequest;
import com.repositorio.mvp.domain.question.DTO.QuestionRequest;
import com.repositorio.mvp.domain.question.DTO.QuestionResponse;
import com.repositorio.mvp.domain.question.mapper.QuestionMapper;
import com.repositorio.mvp.domain.question.model.Question;
import com.repositorio.mvp.domain.question.repository.QuestionRepository;
import com.repositorio.mvp.domain.question.service.QuestionService;
import com.repositorio.mvp.infrastructure.security.UserDetailsImpl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QuestionServiceImpl implements QuestionService {

    private final QuestionRepository questionRepository;
    private final AssetCategoryRepository categoryRepository;
    private final AssetRepository assetRepository;
    private final AssetEvaluationRepository evaluationRepository;
    private final PortfolioRepository portfolioRepository;
    private final QuestionMapper questionMapper;

    @Override
    @Transactional
    public QuestionResponse createQuestion(UUID categoryId, QuestionRequest request) {
        AssetCategory category = getCategoryForCurrentUser(categoryId);
        
        Question question = questionMapper.toEntity(request);
        question.setAssetCategory(category);
        
        return questionMapper.toResponse(questionRepository.save(question));
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuestionResponse> listByCategoryId(UUID categoryId) {
        getCategoryForCurrentUser(categoryId); // Valida posse
        return questionRepository.findAllByAssetCategoryId(categoryId).stream()
                .map(questionMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public QuestionResponse updateQuestion(UUID id, QuestionRequest request) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(MessageConstants.Question.NOT_FOUND));
        
        getCategoryForCurrentUser(question.getAssetCategory().getId()); // Valida posse
        
        question.setText(request.text());
        return questionMapper.toResponse(questionRepository.save(question));
    }

    @Override
    @Transactional
    public void deleteQuestion(UUID id) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(MessageConstants.Question.NOT_FOUND));
        
        getCategoryForCurrentUser(question.getAssetCategory().getId()); // Valida posse
        questionRepository.delete(question);
    }

    @Override
    @Transactional
    public void saveEvaluations(UUID assetId, List<EvaluationRequest> evaluations) {
        Asset asset = assetRepository.findById(assetId)
                .orElseThrow(() -> new EntityNotFoundException(MessageConstants.Asset.NOT_FOUND));
        
        getCategoryForCurrentUser(asset.getCategory().getId()); // Valida posse

        // Remove avaliações antigas para as questões enviadas (ou todas do ativo)
        // Decisão: Substituir as avaliações atuais
        List<AssetEvaluation> currentEvaluations = evaluationRepository.findAllByAssetId(assetId);
        evaluationRepository.deleteAll(currentEvaluations);

        List<AssetEvaluation> newEvaluations = evaluations.stream()
                .map(req -> {
                    Question q = questionRepository.findById(req.questionId())
                            .orElseThrow(() -> new EntityNotFoundException(MessageConstants.Question.NOT_FOUND));
                    
                    return AssetEvaluation.builder()
                            .asset(asset)
                            .question(q)
                            .isPositive(req.isPositive())
                            .build();
                })
                .toList();

        evaluationRepository.saveAll(newEvaluations);
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
