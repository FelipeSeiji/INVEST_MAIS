package com.repositorio.mvp.repository.token;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.repositorio.mvp.model.token.InvalidToken;

@Repository
public interface InvalidatedTokenRepository extends JpaRepository<InvalidToken, String>{}