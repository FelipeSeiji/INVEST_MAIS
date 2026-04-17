package com.repositorio.mvp.domain.question.DTO;

import java.util.UUID;

public record QuestionRequestDTO(
    UUID id,
    String text
) {}
