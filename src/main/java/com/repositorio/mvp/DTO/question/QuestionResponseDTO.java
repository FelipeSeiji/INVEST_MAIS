package com.repositorio.mvp.DTO.question;

import java.util.UUID;

import com.repositorio.mvp.enums.AssetCategory;

public record QuestionResponseDTO(
    UUID id,
    String question,
    String criterion,
    Integer quantity,
    Boolean response,
    String idQuestion,
    AssetCategory categoryActive
) {}
