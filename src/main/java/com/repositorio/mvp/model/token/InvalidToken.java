package com.repositorio.mvp.model.token;

import java.time.Instant;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
public class InvalidToken {
    @Id
    private String token;

    private Instant expiresAt;
}
 