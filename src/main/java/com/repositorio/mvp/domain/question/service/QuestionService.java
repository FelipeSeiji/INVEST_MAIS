package com.repositorio.mvp.domain.question.service;

import java.util.List;
import java.util.UUID;

import com.repositorio.mvp.domain.question.DTO.EvaluationRequestDTO;
import com.repositorio.mvp.domain.question.DTO.QuestionRequestDTO;
import com.repositorio.mvp.domain.question.DTO.QuestionResponseDTO;

public interface QuestionService {
    QuestionResponseDTO createQuestion(UUID categoryId, QuestionRequestDTO request);
    List<QuestionResponseDTO> listByCategoryId(UUID categoryId);
    QuestionResponseDTO updateQuestion(UUID id, QuestionRequestDTO request);
    void deleteQuestion(UUID id);
    
    // Scoring logic
    void saveEvaluations(UUID assetId, List<EvaluationRequestDTO> evaluations);
}
