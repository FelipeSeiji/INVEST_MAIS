package com.repositorio.mvp.domain.user.service;

import java.util.UUID;
import org.springframework.security.core.userdetails.UserDetails;

import com.repositorio.mvp.domain.user.DTO.UserRequestDTO;
import com.repositorio.mvp.domain.user.DTO.UserResponseDTO;
import com.repositorio.mvp.domain.user.DTO.UserUpdateRequestDTO;

public interface UserCommandService {
    UserResponseDTO createUser(UserRequestDTO userRequestDTO);
    void deleteUserById(UUID id);
    UserResponseDTO updateUserById(UUID id, UserUpdateRequestDTO userUpdateRequestDTO);
    UserDetails loadUserDetailsById(String subjectId);
}