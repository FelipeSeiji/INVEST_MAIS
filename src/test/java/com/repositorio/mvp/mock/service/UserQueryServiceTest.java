package com.repositorio.mvp.mock.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import com.repositorio.mvp.common.constants.MessageConstants;
import com.repositorio.mvp.common.result.ServiceResult;
import com.repositorio.mvp.domain.user.DTO.UserResponseDTO;
import com.repositorio.mvp.domain.user.mapper.UserMapper;
import com.repositorio.mvp.domain.user.model.User;
import com.repositorio.mvp.domain.user.model.UserSecurity;
import com.repositorio.mvp.domain.user.repository.UserRepository;
import com.repositorio.mvp.domain.user.service.UserQueryServiceImpl;

import static com.repositorio.mvp.shared.UserConstants.USER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class UserQueryServiceTest {

    @InjectMocks
    private UserQueryServiceImpl userQueryService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    private final UUID userId = UUID.randomUUID();

    @Test
    public void findUserById_WithValidId_ReturnsUserResponseDTO() {
        User user = User.builder()
            .id(userId)
            .name(USER.name())
            .email(USER.email())
            .security(UserSecurity.builder().password(USER.password()).build())
            .build();

        UserResponseDTO expectedResponse =
            new UserResponseDTO(userId, USER.name(), USER.email());

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userMapper.toUserResponseDTO(user)).thenReturn(expectedResponse);

        ServiceResult<UserResponseDTO> result = userQueryService.findUserById(userId);
        UserResponseDTO foundUser = ((ServiceResult.Success<UserResponseDTO>) result).data();

        assertThat(foundUser).isNotNull();
        assertThat(foundUser.id()).isEqualTo(userId);
        assertThat(foundUser.name()).isEqualTo(USER.name());
        assertThat(foundUser.email()).isEqualTo(USER.email());
    }

    @Test
    public void findUserById_WithInvalidId_ThrowException() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        ServiceResult<UserResponseDTO> result = userQueryService.findUserById(userId);
        
        assertThat(result).isInstanceOf(ServiceResult.NotFound.class);
        ServiceResult.NotFound<UserResponseDTO> error = (ServiceResult.NotFound<UserResponseDTO>) result;
        assertThat(error.message()).isEqualTo(MessageConstants.User.NOT_FOUND);
    }

    @Test
    public void listAllUsers_ReturnsPageOfUserResponseDTO() {
        UUID userId2 = UUID.randomUUID();

        User user1 = User.builder()
            .id(userId)
            .name(USER.name())
            .email(USER.email())
            .security(UserSecurity.builder().password(USER.password()).build())
            .build();

        User user2 = User.builder()
            .id(userId2)
            .name("Maria")
            .email("maria@gmail.com")
            .security(UserSecurity.builder().password("1234").build())
            .build();

        UserResponseDTO expectedResponse1 =
            new UserResponseDTO(userId, USER.name(), USER.email());

        UserResponseDTO expectedResponse2 =
            new UserResponseDTO(userId2, "Maria", "maria@gmail.com");

        Pageable pageable = PageRequest.of(0, 20);
        Page<User> usersPage = new PageImpl<>(List.of(user1, user2));

        when(userRepository.findAll(pageable)).thenReturn(usersPage);
        when(userMapper.toUserResponseDTO(user1)).thenReturn(expectedResponse1);
        when(userMapper.toUserResponseDTO(user2)).thenReturn(expectedResponse2);

        Page<UserResponseDTO> resultPage = ((ServiceResult.Success<Page<UserResponseDTO>>) userQueryService.listAllUsers(pageable)).data();
        List<UserResponseDTO> users = resultPage.getContent();

        assertThat(users).isNotNull();
        assertThat(users.size()).isEqualTo(2);
        assertThat(users.get(0).id()).isEqualTo(userId);
        assertThat(users.get(0).name()).isEqualTo(USER.name());
        assertThat(users.get(1).id()).isEqualTo(userId2);
        assertThat(users.get(1).name()).isEqualTo("Maria");
    }
}
