package com.repositorio.mvp.domain.question.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import com.repositorio.mvp.domain.question.DTO.QuestionRequest;
import com.repositorio.mvp.domain.question.DTO.QuestionResponse;
import com.repositorio.mvp.domain.question.model.Question;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface QuestionMapper {

    @Mapping(target = "assetCategory", ignore = true)
    Question toEntity(QuestionRequest request);

    QuestionResponse toResponse(Question question);
}
