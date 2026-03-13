package com.repositorio.mvp.service.interfaces;

import java.util.UUID;
import com.repositorio.mvp.DTO.user.UserRequestDTO;
import com.repositorio.mvp.DTO.user.UserResponseDTO;
import com.repositorio.mvp.DTO.user.UserUpdateRequestDTO;

public interface UserCommandService {
    UserResponseDTO createUser(UserRequestDTO userRequestDTO);
    void deleteUserById(UUID id);
    UserResponseDTO updateUserById(UUID id, UserUpdateRequestDTO userUpdateRequestDTO);
}