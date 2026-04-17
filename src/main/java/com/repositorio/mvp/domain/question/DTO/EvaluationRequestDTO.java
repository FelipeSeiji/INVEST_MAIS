package com.repositorio.mvp.domain.question.DTO;

import java.util.UUID;

public record EvaluationRequestDTO(
    UUID questionId,
    boolean isPositive
) {}
