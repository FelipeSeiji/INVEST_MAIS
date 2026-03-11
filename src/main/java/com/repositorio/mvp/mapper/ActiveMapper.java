package com.repositorio.mvp.mapper;

import org.mapstruct.Mapper;

import com.repositorio.mvp.DTO.active.ActiveRequestDTO;
import com.repositorio.mvp.DTO.active.ActiveResponseDTO;
import com.repositorio.mvp.model.Active;

@Mapper(componentModel = "spring")
public interface ActiveMapper {
    ActiveResponseDTO toActiveResponseDTO(Active active);

    Active toActive(ActiveRequestDTO activeRequestDTO);
}