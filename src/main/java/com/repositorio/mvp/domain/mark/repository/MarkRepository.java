package com.repositorio.mvp.domain.mark.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.repositorio.mvp.domain.mark.model.Mark;

public interface MarkRepository extends JpaRepository<Mark, UUID>{
    
}
