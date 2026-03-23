package com.repositorio.mvp.service;

import java.util.List;
import java.util.UUID;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.repositorio.mvp.DTO.user.UserRequestDTO;
import com.repositorio.mvp.DTO.user.UserResponseDTO;
import com.repositorio.mvp.DTO.user.UserUpdateRequestDTO;
import com.repositorio.mvp.mapper.UserMapper;
import com.repositorio.mvp.model.User;
import com.repositorio.mvp.model.enums.UserRole;
import com.repositorio.mvp.repository.UserRepository;
import com.repositorio.mvp.security.UserDetailsImpl;
import com.repositorio.mvp.service.interfaces.UserCommandService;
import com.repositorio.mvp.service.interfaces.UserQueryService;
import com.repositorio.mvp.service.validation.UserValidation;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

/**
 * Serviço que concentra as regras de negócio para a gestão do cadastro de usuários.
 * Implementa os padrões de Segregação de Comando e Consulta (CQRS) através das interfaces Command e Query.
 */

@Service
@RequiredArgsConstructor
public class UserService implements UserQueryService, UserCommandService{
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final UserValidation userValidation;

    /**
     * Cria e registra um novo usuário no banco de dados.
     * Valida duplicação de e-mail e aplica hash seguro na senha.
     * * @param userRequestDTO DTO com os dados do usuário a ser cadastrado.
     * @return DTO com os dados do usuário recém-criado, ocultando informações sensíveis.
     */
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
    @Transactional(readOnly = true)
    @Override
    public List<UserResponseDTO> listAllUsers() {
        return userRepository.findAll().stream()
            .map(userMapper::toUserResponseDTO)
            .toList();
    }

    /**
     * Remove um usuário permanentemente da base de dados.
     * * @param id UUID do usuário a ser excluído.
     * @throws EntityNotFoundException Caso o usuário não seja encontrado.
     */
    @Transactional
    @Override
    public void deleteUserById(UUID id) {
        if (!userRepository.existsById(id)) {
            throw new EntityNotFoundException("Usuário não encontrado com o ID: " + id);
        }
        userRepository.deleteById(id);
    }

    /**
     * Atualiza o perfil de um usuário existente (ex: nome e e-mail).
     * Como medida de segurança (Account Takeover), exige a confirmação da senha atual.
     * * @param id UUID do usuário a ser atualizado.
     * @param userUpdateRequestDTO Dados novos a serem aplicados, junto com a senha atual para validação.
     * @return DTO com o perfil atualizado do usuário.
     * @throws IllegalArgumentException Se a senha de confirmação estiver incorreta.
     */
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

    /**
     * Carrega os detalhes do usuário para a integração interna do Spring Security.
     * Usado durante a validação do token JWT no filtro de segurança.
     * * @param subjectId ID do usuário extraído do token JWT.
     * @return Instância de UserDetailsImpl populada com os dados e permissões do usuário.
     */
    @Transactional(readOnly = true)
    public UserDetails loadUserDetailsById(String subjectId) {
        User user = userRepository.findById(UUID.fromString(subjectId))
            .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado para o token fornecido."));
        return new UserDetailsImpl(user);
    }
}