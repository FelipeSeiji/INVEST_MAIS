package com.repositorio.mvp.domain.user.service;

import java.util.List;
import java.util.UUID;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.apache.commons.codec.digest.DigestUtils;

import com.repositorio.mvp.domain.user.DTO.UserRequestDTO;
import com.repositorio.mvp.domain.user.DTO.UserResponseDTO;
import com.repositorio.mvp.domain.user.DTO.UserUpdateRequestDTO;
import com.repositorio.mvp.domain.user.mapper.UserMapper;
import com.repositorio.mvp.domain.user.model.User;
import com.repositorio.mvp.domain.user.model.enums.UserRole;
import com.repositorio.mvp.domain.user.repository.UserRepository;
import com.repositorio.mvp.domain.user.service.interfaces.UserCommandService;
import com.repositorio.mvp.domain.user.validation.interfaces.UserRegisterValidator;
import com.repositorio.mvp.domain.user.validation.interfaces.UserUpdateValidator;
import com.repositorio.mvp.infrastructure.security.UserDetailsImpl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserCommandServiceImpl implements UserCommandService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final List<UserRegisterValidator> registerValidators;
    private final List<UserUpdateValidator> updateValidators;

    /**
     * Cria e registra um novo usuário no banco de dados.
     * Valida duplicação de e-mail e aplica hash seguro na senha.
     * * @param userRequestDTO DTO com os dados do usuário a ser cadastrado.
     * @return DTO com os dados do usuário recém-criado, ocultando informações sensíveis.
     */
    @Override
    @Transactional
    public UserResponseDTO createUser(UserRequestDTO userRequestDTO) {
        registerValidators.forEach(v -> v.validate(userRequestDTO));

        User user = userMapper.toUser(userRequestDTO);
        user.setPassword(passwordEncoder.encode(userRequestDTO.password()));
        user.setEmailHash(DigestUtils.sha256Hex(userRequestDTO.email().toLowerCase()));
        user.setRole(UserRole.USER);
        userRepository.save(user);

        return userMapper.toUserResponseDTO(user);
    }

    /**
     * Remove um usuário permanentemente da base de dados.
     * * @param id UUID do usuário a ser excluído.
     * @throws EntityNotFoundException Caso o usuário não seja encontrado.
     */
    @Override
    @Transactional
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
    @Override
    @Transactional
    public UserResponseDTO updateUserById(UUID id, UserUpdateRequestDTO userUpdateRequestDTO) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado."));
        
        if (!passwordEncoder.matches(userUpdateRequestDTO.currentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("A senha atual informada está incorreta.");
        }
        
        updateValidators.forEach(v -> v.validate(userUpdateRequestDTO, user));

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
