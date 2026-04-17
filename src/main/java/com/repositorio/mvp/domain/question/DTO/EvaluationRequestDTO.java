package com.repositorio.mvp.domain.question.DTO;

import java.util.UUID;

import io.swagger.v3.oas.annotations.media.Schema;
import com.repositorio.mvp.common.validation.question.ValidQuestionId;
import com.repositorio.mvp.common.validation.question.ValidIsPositive;

public record EvaluationRequestDTO(
        @ValidQuestionId 
        @Schema(description = "ID único da pergunta", example = "123e4567-e89b-12d3-a456-426614174000") 
        UUID questionId,
        @ValidIsPositive 
        @Schema(description = "Avaliação da pergunta", example = "true") 
        Boolean isPositive) {
}
