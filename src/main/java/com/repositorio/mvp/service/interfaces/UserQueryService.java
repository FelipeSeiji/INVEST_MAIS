package com.repositorio.mvp.service.interfaces;

import java.util.List;
import java.util.UUID;

import com.repositorio.mvp.DTO.user.UserResponseDTO;

public interface UserQueryService {
    UserResponseDTO findUserById(UUID id);
    List<UserResponseDTO> listAllUsers();
}
