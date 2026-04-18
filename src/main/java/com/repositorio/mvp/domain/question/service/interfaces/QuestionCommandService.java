package com.repositorio.mvp.domain.question.service.interfaces;

import java.util.List;
import java.util.UUID;

import com.repositorio.mvp.domain.question.DTO.EvaluationRequestDTO;
import com.repositorio.mvp.domain.question.DTO.QuestionRequestDTO;
import com.repositorio.mvp.domain.question.DTO.QuestionResponseDTO;

import lombok.NonNull;

public interface QuestionCommandService {
    QuestionResponseDTO createQuestion(@NonNull UUID categoryId, @NonNull QuestionRequestDTO request);
    QuestionResponseDTO updateQuestion(@NonNull UUID id, @NonNull QuestionRequestDTO request);
    void deleteQuestion(@NonNull UUID id);
    void saveEvaluations(@NonNull UUID assetId, @NonNull List<EvaluationRequestDTO> evaluations);
}
