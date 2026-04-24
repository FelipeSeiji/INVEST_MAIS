package com.repositorio.mvp.domain.user.service.interfaces;

import java.util.UUID;

import org.springframework.security.core.userdetails.UserDetails;

import com.repositorio.mvp.common.result.ServiceResult;
import com.repositorio.mvp.domain.user.DTO.UserRequestDTO;
import com.repositorio.mvp.domain.user.DTO.UserResponseDTO;
import com.repositorio.mvp.domain.user.DTO.UserUpdateRequestDTO;

public interface UserCommandService {
    ServiceResult<UserResponseDTO> createUser(UserRequestDTO userRequestDTO);
    ServiceResult<Void> deleteUserById(UUID id);
    ServiceResult<UserResponseDTO> updateUserById(UUID id, UserUpdateRequestDTO userUpdateRequestDTO);
    UserDetails loadUserDetailsById(String subjectId);
}