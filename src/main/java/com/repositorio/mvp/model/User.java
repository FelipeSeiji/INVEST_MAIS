package com.repositorio.mvp.model;

import java.time.LocalDateTime;
import java.util.UUID;

import com.repositorio.mvp.model.enums.UserRole;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "TB_USER")
@Getter
@Setter
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

    @Column(name = "two_factor_code", length = 6)
    private String twoFactorCode;

    @Column(name = "two_factor_expiry")
    private LocalDateTime twoFactorExpiry;

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