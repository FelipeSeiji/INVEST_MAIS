package com.repositorio.mvp.mock.service;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import com.repositorio.mvp.common.constants.MessageConstants;
import com.repositorio.mvp.common.result.ServiceResult;
import com.repositorio.mvp.common.security.CryptoService;
import com.repositorio.mvp.domain.portfolio.service.interfaces.PortfolioCommandService;
import com.repositorio.mvp.domain.user.DTO.UserRequestDTO;
import com.repositorio.mvp.domain.user.DTO.UserResponseDTO;
import com.repositorio.mvp.domain.user.mapper.UserMapper;
import com.repositorio.mvp.domain.user.model.User;
import com.repositorio.mvp.domain.user.model.UserSecurity;
import com.repositorio.mvp.domain.user.repository.UserRepository;
import com.repositorio.mvp.domain.user.service.UserCommandServiceImpl;
import com.repositorio.mvp.domain.user.validation.interfaces.UserRegisterValidator;

import static com.repositorio.mvp.shared.UserConstants.INVALID_USER;
import static com.repositorio.mvp.shared.UserConstants.USER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserCommandServiceTest {

    @InjectMocks
    private UserCommandServiceImpl userCommandService;

    @Mock
    private UserRepository userRepository;
    
    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserMapper userMapper;

    @Mock
    private UserRegisterValidator userRegisterValidator;

    @Mock
    private PortfolioCommandService portfolioCommandService;

    @Mock
    private CryptoService cryptoService;

    private final UUID userId = UUID.randomUUID();

    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(
            userCommandService, 
            "registerValidators",
            List.of(userRegisterValidator)
        );
        lenient().when(cryptoService.generateSha256Hash(anyString())).thenReturn("hashed_email");
    }

    @Test
    public void createUser_WithValidData_ReturnsUserResponseDTO() {
        User user = User.builder()
            .id(userId)
            .name(USER.name())
            .email(USER.email())
            .security(UserSecurity.builder().password("senha_encriptada").build())
            .build();

        UserResponseDTO expectedResponse =
            new UserResponseDTO(userId, USER.name(), USER.email());

        when(userMapper.toUser(any(UserRequestDTO.class))).thenReturn(user);
        when(passwordEncoder.encode(anyString())).thenReturn("senha_encriptada");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(userMapper.toUserResponseDTO(any(User.class))).thenReturn(expectedResponse);

        ServiceResult<UserResponseDTO> result = userCommandService.createUser(USER);
        UserResponseDTO createdUser = ((ServiceResult.Success<UserResponseDTO>) result).data();

        assertThat(createdUser).isNotNull();
        assertThat(createdUser.id()).isEqualTo(userId);
        assertThat(createdUser.name()).isEqualTo(USER.name());
        assertThat(createdUser.email()).isEqualTo(USER.email());
    }

    @Test
    public void createUser_WithEmailAlreadyInUse_ReturnsError() {
        doThrow(new IllegalArgumentException(MessageConstants.User.EMAIL_ALREADY_IN_USE))
            .when(userRegisterValidator)
            .validate(any(UserRequestDTO.class));

        ServiceResult<UserResponseDTO> result = userCommandService.createUser(INVALID_USER);
        
        assertThat(result).isInstanceOf(ServiceResult.Error.class);
        assertThat(((ServiceResult.Error<UserResponseDTO>) result).message()).isEqualTo(MessageConstants.User.EMAIL_ALREADY_IN_USE);
    }
}
