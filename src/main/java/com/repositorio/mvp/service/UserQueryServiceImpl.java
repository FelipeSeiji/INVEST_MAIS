package com.repositorio.mvp.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.repositorio.mvp.DTO.user.UserResponseDTO;
import com.repositorio.mvp.mapper.UserMapper;
import com.repositorio.mvp.repository.UserRepository;
import com.repositorio.mvp.service.interfaces.UserQueryService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserQueryServiceImpl implements UserQueryService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    /**
     * Busca um usuário específico pelo seu identificador único (UUID).
     * * @param id UUID do usuário.
     * @return DTO contendo os dados do usuário.
     * @throws EntityNotFoundException Se o UUID não existir na base de dados.
     */
    @Override
    @Transactional(readOnly = true)
    public UserResponseDTO findUserById(UUID id) {
        return userRepository.findById(id)
            .map(userMapper::toUserResponseDTO)
            .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));
    }

    /**
     * Lista todos os usuários cadastrados no sistema.
     * Nota: Idealmente, em produção, este método deve implementar paginação (Pageable).
     * * @return Lista contendo DTOs de todos os usuários.
     */
    @Override
    @Transactional(readOnly = true)
    public List<UserResponseDTO> listAllUsers() {
        return userRepository.findAll().stream()
            .map(userMapper::toUserResponseDTO)
            .toList();
    }
}
