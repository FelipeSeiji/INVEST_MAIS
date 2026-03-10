package com.repositorio.mvp.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.repositorio.mvp.model.Active;

@Repository
public interface QuestionRepository extends JpaRepository<Active, UUID>{
    
}
