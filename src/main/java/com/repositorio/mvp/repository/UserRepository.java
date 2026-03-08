package com.repositorio.mvp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.repositorio.mvp.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}
