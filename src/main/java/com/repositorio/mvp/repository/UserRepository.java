package com.repositorio.mvp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.repositorio.mvp.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
}
