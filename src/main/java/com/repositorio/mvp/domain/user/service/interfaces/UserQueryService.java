package com.repositorio.mvp.domain.user.service.interfaces;

import java.util.List;
import java.util.UUID;

import com.repositorio.mvp.domain.user.DTO.UserResponseDTO;

public interface UserQueryService {
    UserResponseDTO findUserById(UUID id);
    List<UserResponseDTO> listAllUsers();
}
