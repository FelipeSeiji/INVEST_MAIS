package com.repositorio.mvp.shared;

import com.repositorio.mvp.DTO.user.UserRequestDTO;

public class UserConstants {
    public static final UserRequestDTO USER = new UserRequestDTO(
        "Felipe",
        "felipe@gmail.com",
        "1234"
    );

    public static final UserRequestDTO INVALID_USER = new UserRequestDTO(
        "",
        "invalid-email",
        ""
    );
}
