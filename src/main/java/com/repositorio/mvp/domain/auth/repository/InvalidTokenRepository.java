package com.repositorio.mvp.domain.auth.repository;

import java.time.Instant;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.repositorio.mvp.domain.auth.model.InvalidToken;

@Repository
public interface InvalidTokenRepository extends JpaRepository<InvalidToken, String>{    
    void deleteByExpiresAtBefore(Instant now);
}