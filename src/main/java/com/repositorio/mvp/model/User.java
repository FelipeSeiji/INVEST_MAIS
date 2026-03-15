package com.repositorio.mvp.model;

import java.time.LocalDateTime;
import java.util.UUID;
import com.repositorio.mvp.enums.UserRole;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "TB_USER")
@Getter
@Setter // Usado com cuidado, prefira métodos de negócio
@ToString
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Setter(AccessLevel.NONE)
    private UUID id;

    @NotBlank
    @Column(nullable = false, length = 50)
    private String name;

    @NotBlank
    @Email
    @Column(nullable = false, unique = true, length = 50)
    private String email;

    @NotBlank
    @Column(nullable = false, length = 60)
    @ToString.Exclude
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    // --- NOVOS CAMPOS PARA 2FA ---
    @Column(name = "two_factor_code", length = 6)
    private String twoFactorCode;

    @Column(name = "two_factor_expiry")
    private LocalDateTime twoFactorExpiry;

    // Métodos de negócio (Evitando setters diretos para regras)
    public void generateTwoFactorCode(String code, LocalDateTime expiry) {
        this.twoFactorCode = code;
        this.twoFactorExpiry = expiry;
    }

    public void clearTwoFactorCode() {
        this.twoFactorCode = null;
        this.twoFactorExpiry = null;
    }

    public void updateProfile(String name, String email) {
        this.name = name;
        this.email = email;
    }
}