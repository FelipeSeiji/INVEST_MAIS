package com.repositorio.mvp.domain.auth.model;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Getter;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "TB_INVALID_TOKEN")
@Getter
@Setter
public class InvalidToken {
    @Id
    @Column(length = 64) // SHA-256 hex hash tem 64 caracteres
    private String token; // Armazena o SHA-256 hash do token JWT

    private Instant expiresAt;
}
 