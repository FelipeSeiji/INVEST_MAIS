package com.repositorio.mvp.domain.question.service.interfaces;

import java.util.List;
import java.util.UUID;

import com.repositorio.mvp.common.result.ServiceResult;
import com.repositorio.mvp.domain.question.DTO.QuestionResponseDTO;

import lombok.NonNull;

public interface QuestionQueryService {
    ServiceResult<List<QuestionResponseDTO>> listByCategoryId(@NonNull UUID categoryId);
}
