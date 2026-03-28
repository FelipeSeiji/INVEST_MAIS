package com.repositorio.mvp.model.token;

import java.time.Instant;

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
    private String token;

    private Instant expiresAt;
}
 