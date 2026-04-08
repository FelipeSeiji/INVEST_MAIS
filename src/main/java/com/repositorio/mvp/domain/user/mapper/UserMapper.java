package com.repositorio.mvp.domain.user.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.repositorio.mvp.domain.user.DTO.UserRequestDTO;
import com.repositorio.mvp.domain.user.DTO.UserResponseDTO;
import com.repositorio.mvp.domain.user.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserResponseDTO toUserResponseDTO(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "security", ignore = true)
    User toUser(UserRequestDTO userRequestDTO);
}
