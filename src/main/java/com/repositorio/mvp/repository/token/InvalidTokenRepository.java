package com.repositorio.mvp.repository.token;

import java.time.Instant;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.repositorio.mvp.model.token.InvalidToken;

@Repository
public interface InvalidTokenRepository extends JpaRepository<InvalidToken, String>{    
    void deleteByExpiresAtBefore(Instant now);
}