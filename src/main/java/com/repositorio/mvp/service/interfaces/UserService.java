package com.repositorio.mvp.service.interfaces;

import java.util.List;
import java.util.UUID;

import com.repositorio.mvp.DTO.user.UserRequestDTO;
import com.repositorio.mvp.DTO.user.UserResponseDTO;
import com.repositorio.mvp.DTO.user.UserUpdateRequestDTO;

public interface UserService {
    UserResponseDTO createUser(UserRequestDTO userRequestDTO);
    UserResponseDTO findUserById(UUID id);
    List<UserResponseDTO> listAllUsers();
    void deleteUserById(UUID id);
    UserResponseDTO updateByIdUser(UUID id, UserUpdateRequestDTO userUpdateRequestDTO);
}