package com.repositorio.mvp.domain.user.repository;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.repositorio.mvp.domain.user.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    boolean existsBySecurityEmailHash(String emailHash);

    Optional<User> findByEmail(String email);

    Optional<User> findBySecurityEmailHash(String emailHash);
}
