package com.repositorio.mvp.domain.user.service.interfaces;

import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.repositorio.mvp.domain.user.DTO.UserResponseDTO;

public interface UserQueryService {
    UserResponseDTO findUserById(UUID id);
    Page<UserResponseDTO> listAllUsers(Pageable pageable);
}
