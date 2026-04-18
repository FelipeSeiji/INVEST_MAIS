package com.repositorio.mvp.domain.question.service.interfaces;

import java.util.List;
import java.util.UUID;

import com.repositorio.mvp.domain.question.DTO.QuestionResponseDTO;

import lombok.NonNull;

public interface QuestionQueryService {
    List<QuestionResponseDTO> listByCategoryId(@NonNull UUID categoryId);
}
