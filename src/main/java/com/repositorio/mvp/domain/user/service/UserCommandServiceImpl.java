package com.repositorio.mvp.domain.user.service;

import java.util.List;
import java.util.UUID;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.apache.commons.codec.digest.DigestUtils;

import com.repositorio.mvp.common.constants.MessageConstants;
import com.repositorio.mvp.domain.portfolio.service.interfaces.PortfolioCommandService;
import com.repositorio.mvp.domain.user.DTO.UserRequestDTO;
import com.repositorio.mvp.domain.user.DTO.UserResponseDTO;
import com.repositorio.mvp.domain.user.DTO.UserUpdateRequestDTO;
import com.repositorio.mvp.domain.user.mapper.UserMapper;
import com.repositorio.mvp.domain.user.model.User;
import com.repositorio.mvp.domain.user.model.UserSecurity;
import com.repositorio.mvp.domain.user.model.enums.UserRole;
import com.repositorio.mvp.domain.user.repository.UserRepository;
import com.repositorio.mvp.domain.user.service.interfaces.UserCommandService;
import com.repositorio.mvp.domain.user.validation.interfaces.UserRegisterValidator;
import com.repositorio.mvp.domain.user.validation.interfaces.UserUpdateValidator;
import com.repositorio.mvp.infrastructure.security.UserDetailsImpl;

import jakarta.persistence.EntityNotFoundException;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.repositorio.mvp.common.constants.LogMessageConstants;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserCommandServiceImpl implements UserCommandService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final PortfolioCommandService portfolioCommandService;
    private final List<UserRegisterValidator> registerValidators;
    private final List<UserUpdateValidator> updateValidators;

    /**
     * Cria e registra um novo usuário no banco de dados.
     * Realiza validações de negócio, gera o hash seguro da senha e o hash de busca do e-mail.
     * 
     * @param userRequestDTO DTO contendo os dados do prospecto (nome, e-mail, senha).
     * @return DTO com os dados do usuário persistido, ocultando informações de segurança.
     * @throws ValidationException Caso as regras de negócio de registro sejam violadas.
     */
    @Override
    @Transactional
    public UserResponseDTO createUser(@NonNull UserRequestDTO userRequestDTO) {
        registerValidators.forEach(v -> v.validate(userRequestDTO));

        User user = userMapper.toUser(userRequestDTO);
        UserSecurity security = UserSecurity.builder()
            .password(passwordEncoder.encode(userRequestDTO.password()))
            .emailHash(DigestUtils.sha256Hex(userRequestDTO.email().toLowerCase()))
            .role(UserRole.USER)
            .build();
        user.setSecurity(security);
        userRepository.save(user);
        log.info(LogMessageConstants.AUDIT.USER_CREATED, user.getId(), user.getEmail());

        // Inicializa a carteira vazia para o novo usuário
        portfolioCommandService.createPortfolioForUser(user.getId());

        return userMapper.toUserResponseDTO(user);
    }

    /**
     * Remove permanentemente um usuário da base de dados.
     * 
     * @param id UUID do usuário que deve ser excluído.
     * @throws EntityNotFoundException Caso o identificador não seja localizado.
     */
    @Override
    @Transactional
    public void deleteUserById(@NonNull UUID id) {
        if (!userRepository.existsById(id)) {
            throw new EntityNotFoundException(MessageConstants.User.NOT_FOUND_WITH_ID + id);
        }
        userRepository.deleteById(id);
        log.info(LogMessageConstants.AUDIT.USER_DELETED, id);
    }

    /**
     * Atualiza o perfil (nome e e-mail) de um usuário existente.
     * Medida de segurança: Exige a senha atual para autorizar alterações críticas no perfil.
     * 
     * @param id UUID do usuário a ser atualizado.
     * @param userUpdateRequestDTO Novos dados do perfil e senha de confirmação.
     * @return DTO com o perfil atualizado do usuário.
     * @throws IllegalArgumentException Se a senha atual informada estiver incorreta.
     * @throws EntityNotFoundException Se o usuário não for encontrado.
     */
    @Override
    @Transactional
    public UserResponseDTO updateUserById(@NonNull UUID id, @NonNull UserUpdateRequestDTO userUpdateRequestDTO) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new EntityNotFoundException(
                MessageConstants.User.NOT_FOUND_FOR_UPDATE
            ));
        
        if (!passwordEncoder.matches(userUpdateRequestDTO.currentPassword(), user.getSecurity().getPassword())) {
            throw new IllegalArgumentException(
                MessageConstants.User.INVALID_PASSWORD
            );
        }
        
        updateValidators.forEach(v -> v.validate(userUpdateRequestDTO, user));
        
        user.updateProfile(userUpdateRequestDTO.name(), userUpdateRequestDTO.email());

        User updatedUser = userRepository.save(user);
        log.info(LogMessageConstants.AUDIT.USER_UPDATED, id);
        return userMapper.toUserResponseDTO(updatedUser);
    }

    /**
     * Localiza e carrega os detalhes de segurança do usuário para o Spring Security.
     * Método central para a autorização stateless via Token JWT.
     * 
     * @param subjectId ID do usuário (como String) extraído do assunto do Token.
     * @return Objeto UserDetailsImpl compatível com o ecossistema de segurança do Spring.
     * @throws IllegalArgumentException Caso o ID no token não corresponda a um usuário ativo.
     */
    @Transactional(readOnly = true)
    public UserDetails loadUserDetailsById(@NonNull String subjectId) {
        User user = userRepository.findById(UUID.fromString(subjectId))
            .orElseThrow(() -> new IllegalArgumentException(
                MessageConstants.User.NOT_FOUND_FOR_TOKEN
            ));
        return new UserDetailsImpl(user);
    }
}
