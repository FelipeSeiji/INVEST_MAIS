package com.repositorio.mvp.domain.question.DTO;

import java.util.UUID;

public record QuestionResponseDTO(
    UUID id,
    String text
) {}
