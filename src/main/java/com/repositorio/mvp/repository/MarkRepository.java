package com.repositorio.mvp.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.repositorio.mvp.model.Mark;

public interface MarkRepository extends JpaRepository<Mark, UUID>{
    
}
