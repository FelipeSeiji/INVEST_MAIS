package com.repositorio.mvp.domain.user.model;

import java.util.UUID;

import org.apache.commons.codec.digest.DigestUtils;

import com.repositorio.mvp.infrastructure.util.AttributeEncryptor;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
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
@ToString(onlyExplicitlyIncluded = true)
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Setter(AccessLevel.NONE)
    @ToString.Include
    private UUID id;

    @NotBlank
    @Column(nullable = false, length = 255)
    @Convert(converter = AttributeEncryptor.class)
    private String name;

    @NotBlank
    @Email
    @Column(nullable = false, unique = true, length = 500)
    @Convert(converter = AttributeEncryptor.class)
    private String email;

    @Embedded
    private UserSecurity security;

    public void updateProfile(String name, String email) {
        this.name = name;
        this.email = email;
        if (this.security != null) {
            this.security.setEmailHash(DigestUtils.sha256Hex(email.toLowerCase()));
        }
    }
}