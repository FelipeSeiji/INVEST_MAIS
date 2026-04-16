package com.repositorio.mvp.domain.question.DTO;

import java.util.UUID;

public record EvaluationRequest(
    UUID questionId,
    boolean isPositive
) {}
