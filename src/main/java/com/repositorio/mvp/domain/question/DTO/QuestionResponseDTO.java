package com.repositorio.mvp.domain.question.DTO;

import java.util.UUID;

import com.repositorio.mvp.domain.asset.model.enums.AssetCategory;

public record QuestionResponseDTO(
    UUID id,
    String question,
    String criterion,
    Integer quantity,
    Boolean response,
    String idQuestion,
    AssetCategory categoryActive
) {}
