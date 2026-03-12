package com.repositorio.mvp.model;

import java.time.Instant;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class InvalidatedToken {

    @Id
    private String token;

    private Instant expiresAt;

    public InvalidatedToken(){}

    public InvalidatedToken(String token, Instant expiresAt){
        this.token = token;
        this.expiresAt = expiresAt;
    }
}