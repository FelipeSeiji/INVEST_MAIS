package com.repositorio.mvp.domain.user.service;

import java.util.List;
import java.util.UUID;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.repositorio.mvp.common.constants.LogMessageConstants;
import com.repositorio.mvp.common.constants.MessageConstants;
import com.repositorio.mvp.common.result.ServiceResult;
import com.repositorio.mvp.common.security.CryptoService;
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

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserCommandServiceImpl implements UserCommandService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final PortfolioCommandService portfolioCommandService;
    private final CryptoService cryptoService;
    private final List<UserRegisterValidator> registerValidators;
    private final List<UserUpdateValidator> updateValidators;

    /**
     * Cria e registra um novo usuário no banco de dados.
     * Realiza validações de negócio, gera o hash seguro da senha e o hash de busca do e-mail.
     * 
     * @param userRequestDTO DTO contendo os dados do prospecto (nome, e-mail, senha).
     * @return DTO com os dados do usuário persistido, ocultando informações de segurança.
     */
    @Override
    @Transactional
    public ServiceResult<UserResponseDTO> createUser(@NonNull UserRequestDTO userRequestDTO) {
        try {
            registerValidators.forEach(v -> v.validate(userRequestDTO));

            User user = userMapper.toUser(userRequestDTO);
            UserSecurity security = UserSecurity.builder()
                .password(passwordEncoder.encode(userRequestDTO.password()))
                .emailHash(cryptoService.generateSha256Hash(userRequestDTO.email()))
                .role(UserRole.USER)
                .emailVerified(false)
                .build();
            user.setSecurity(security);
            userRepository.save(user);
            log.info(LogMessageConstants.AUDIT.USER_CREATED, user.getId(), user.getEmail());

            // Inicializa a carteira vazia para o novo usuário
            portfolioCommandService.createPortfolioForUser(user.getId());

            return ServiceResult.success(userMapper.toUserResponseDTO(user));
        } catch (DataIntegrityViolationException _) {
            log.warn("Tentativa de registro duplicado omitida para segurança.");
            return ServiceResult.error(MessageConstants.User.EMAIL_ALREADY_IN_USE);
        } catch (IllegalArgumentException e) {
            return ServiceResult.error(e.getMessage());
        } catch (Exception e) {
            log.error("Erro inesperado no registro de usuário: {}", e.getMessage());
            return ServiceResult.error("Ocorreu um erro ao processar seu cadastro. Tente novamente mais tarde.");
        }
    }

    /**
     * Remove permanentemente um usuário da base de dados.
     * 
     * @param id UUID do usuário que deve ser excluído.
     */
    @Override
    @Transactional
    public ServiceResult<Void> deleteUserById(@NonNull UUID id) {
        if (!userRepository.existsById(id)) {
            return ServiceResult.notFound(MessageConstants.User.NOT_FOUND_WITH_ID + id);
        }
        userRepository.deleteById(id);
        log.info(LogMessageConstants.AUDIT.USER_DELETED, id);
        return ServiceResult.success(null);
    }

    /**
     * Atualiza o perfil (nome e e-mail) de um usuário existente.
     * Medida de segurança: Exige a senha atual para autorizar alterações críticas no perfil.
     * 
     * @param id UUID do usuário a ser atualizado.
     * @param userUpdateRequestDTO Novos dados do perfil e senha de confirmação.
     * @return DTO com o perfil atualizado do usuário.
     */
    @Override
    @Transactional
    public ServiceResult<UserResponseDTO> updateUserById(@NonNull UUID id, @NonNull UserUpdateRequestDTO userUpdateRequestDTO) {
        return userRepository.findById(id)
            .map(user -> {
                if (!passwordEncoder.matches(userUpdateRequestDTO.currentPassword(), user.getSecurity().getPassword())) {
                    return ServiceResult.<UserResponseDTO>error(MessageConstants.User.INVALID_PASSWORD);
                }
                
                try {
                    updateValidators.forEach(v -> v.validate(userUpdateRequestDTO, user));
                    String emailHash = cryptoService.generateSha256Hash(userUpdateRequestDTO.email());
                    user.updateProfile(userUpdateRequestDTO.name(), userUpdateRequestDTO.email(), emailHash);
                    User updatedUser = userRepository.save(user);
                    log.info(LogMessageConstants.AUDIT.USER_UPDATED, id);
                    return ServiceResult.success(userMapper.toUserResponseDTO(updatedUser));
                } catch (Exception e) {
                    return ServiceResult.<UserResponseDTO>error(e.getMessage());
                }
            })
            .orElseGet(() -> ServiceResult.notFound(MessageConstants.User.NOT_FOUND_FOR_UPDATE));
    }
}
