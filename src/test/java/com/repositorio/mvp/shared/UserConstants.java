package com.repositorio.mvp.shared;

import java.util.UUID;

import com.repositorio.mvp.domain.user.DTO.UserRequestDTO;
import com.repositorio.mvp.domain.user.model.User;

public class UserConstants {
    public static final UserRequestDTO USER = new UserRequestDTO(
        "User",
        "example@gmail.com",
        "Password@123"
    );

    public static final UserRequestDTO INVALID_USER = new UserRequestDTO(
        "",
        "invalid-email",
        ""
    );

    public static User createMockUser() {
        return User.builder()
        .id(UUID.randomUUID())
        .name(USER.name())
        .email(USER.email())
        .security(com.repositorio.mvp.domain.user.model.UserSecurity.builder().password("password").build())
        .build();
    }
}
