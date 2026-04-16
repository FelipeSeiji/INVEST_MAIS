package com.repositorio.mvp.domain.question.DTO;

import java.util.UUID;

public record QuestionRequest(
    UUID id,
    String text
) {}
