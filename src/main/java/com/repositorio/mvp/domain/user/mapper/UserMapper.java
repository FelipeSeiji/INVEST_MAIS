package com.repositorio.mvp.domain.user.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.repositorio.mvp.domain.user.DTO.UserRequestDTO;
import com.repositorio.mvp.domain.user.DTO.UserResponseDTO;
import com.repositorio.mvp.domain.user.model.User;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "UserMapper", description = "Mapper para conversão entre User e DTOs")
@Mapper(componentModel = "spring")
public interface UserMapper {
    UserResponseDTO toUserResponseDTO(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "security", ignore = true)
    User toUser(UserRequestDTO userRequestDTO);
}
