package com.repositorio.mvp.domain.question.DTO;

import java.util.UUID;

public record QuestionResponse(
    UUID id,
    String text
) {}
