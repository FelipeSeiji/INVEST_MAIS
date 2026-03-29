package com.repositorio.mvp.domain.user.model;

import java.time.LocalDateTime;
import java.util.UUID;

import org.apache.commons.codec.digest.DigestUtils;

import com.repositorio.mvp.domain.user.model.enums.UserRole;
import com.repositorio.mvp.infrastructure.util.AttributeEncryptor;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
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
    @Column(nullable = false, length = 255)
    @Convert(converter = AttributeEncryptor.class)
    @ToString.Exclude
    private String name;

    @NotBlank
    @Email
    @Column(nullable = false, unique = true, length = 500)
    @Convert(converter = AttributeEncryptor.class)
    @ToString.Exclude
    private String email;

    @NotBlank
    @Column(nullable = false, length = 255)
    @ToString.Exclude
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    @Column(name = "two_factor_code", length = 6)
    @ToString.Exclude
    private String twoFactorCode;

    @Column(name = "two_factor_expiry")
    private LocalDateTime twoFactorExpiry;

    @Column(nullable = false, unique = true, length = 64)
    private String emailHash;

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
        this.emailHash = DigestUtils.sha256Hex(email.toLowerCase());
    }
}