package com.repositorio.mvp.domain.question.service;

import java.util.List;
import java.util.UUID;

import com.repositorio.mvp.domain.question.DTO.EvaluationRequest;
import com.repositorio.mvp.domain.question.DTO.QuestionRequest;
import com.repositorio.mvp.domain.question.DTO.QuestionResponse;

public interface QuestionService {
    QuestionResponse createQuestion(UUID categoryId, QuestionRequest request);
    List<QuestionResponse> listByCategoryId(UUID categoryId);
    QuestionResponse updateQuestion(UUID id, QuestionRequest request);
    void deleteQuestion(UUID id);
    
    // Scoring logic
    void saveEvaluations(UUID assetId, List<EvaluationRequest> evaluations);
}
