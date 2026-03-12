package com.repositorio.mvp.DTO.register;

import com.repositorio.mvp.model.UserRole;

public record RegisterDTO(String name, String email, String password, UserRole role) {


}
