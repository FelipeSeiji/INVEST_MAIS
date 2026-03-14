package com.repositorio.mvp.service;

import java.util.List;
import java.util.UUID;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.repositorio.mvp.DTO.user.UserRequestDTO;
import com.repositorio.mvp.DTO.user.UserResponseDTO;
import com.repositorio.mvp.DTO.user.UserUpdateRequestDTO;
import com.repositorio.mvp.enums.UserRole;
import com.repositorio.mvp.mapper.UserMapper;
import com.repositorio.mvp.model.User;
import com.repositorio.mvp.model.validation.UserValidation;
import com.repositorio.mvp.repository.UserRepository;
import com.repositorio.mvp.service.interfaces.UserCommandService;
import com.repositorio.mvp.service.interfaces.UserQueryService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

/* Classe de serviço para todos os processos e regras de negocio da entidade USER
 */

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserQueryService, UserCommandService{
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final UserValidation userValidation;

    //Metodo para criar um novo usuário
    @Override
    @Transactional
    public UserResponseDTO createUser(UserRequestDTO userRequestDTO) {
        userValidation.validadeNewEmail(userRequestDTO.email());

        User user = userMapper.toUser(userRequestDTO);
        user.setPassword(passwordEncoder.encode(userRequestDTO.password()));
        user.setRole(UserRole.USER);
        userRepository.save(user);

        return userMapper.toUserResponseDTO(user);
    }

    //Metodo para buscar um usuário por ID
    @Override
    @Transactional(readOnly = true)
    public UserResponseDTO findUserById(UUID id) {
        return userRepository.findById(id)
            .map(userMapper::toUserResponseDTO)
            .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));
    }

    //Metodo para listar todos os usuários
    @Transactional(readOnly = true)
    @Override
    public List<UserResponseDTO> listAllUsers() {
        return userRepository.findAll().stream()
            .map(userMapper::toUserResponseDTO)
            .toList();
    }

    //Metodo para excluir um usuário por ID
    @Transactional
    @Override
    public void deleteUserById(UUID id) {
        if (!userRepository.existsById(id)) {
            throw new EntityNotFoundException("Usuário não encontrado com o ID: " + id);
        }
        userRepository.deleteById(id);
    }

    //Metodo para atualizar um usuário por ID
    @Transactional
    @Override
    public UserResponseDTO updateUserById(UUID id, UserUpdateRequestDTO userUpdateRequestDTO) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado."));

        userValidation.validadeUpdateEmail(userUpdateRequestDTO.email(), user.getEmail());

        user.setName(userUpdateRequestDTO.name());
        user.setEmail(userUpdateRequestDTO.email());

        return userMapper.toUserResponseDTO(userRepository.save(user));
    }
}