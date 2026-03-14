package com.repositorio.mvp.model;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "TB_USER")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Builder
@AllArgsConstructor
public class User implements UserDetails{
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Setter(AccessLevel.NONE)
    private UUID id;

    @NotBlank
    @Column(nullable = false, length = 50)
    @Size(min = 8, max = 50)
    private String name;

    @NotBlank
    @Email
    @Column(nullable = false, unique = true, length = 50)
    @Size(min = 8, max = 50)
    private String email;

    @NotBlank
    @Column(nullable = false, length = 50)
    @Size(max = 80)
    @ToString.Exclude
    private String password;

    @NotNull
    @Column(nullable = false)
    private UserRole role;

    @Column(nullable = false)
    private boolean mfaEnabled = false;

    @Column(length = 32)
    private String mfaSecret;

    public User(String name, String email, String password, UserRole role){
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if(this.role == UserRole.ADMIN) return List.of(new SimpleGrantedAuthority("ROLE_ADMIN"), new SimpleGrantedAuthority("ROLE_USER")); 
        else return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() { return true; }//futura implementação

    @Override
    public boolean isAccountNonLocked() { return true; }//futura implementação

    @Override
    public boolean isCredentialsNonExpired() { return true; }//futura implementação

    @Override
    public boolean isEnabled() { return true; }//futura implementação
}
