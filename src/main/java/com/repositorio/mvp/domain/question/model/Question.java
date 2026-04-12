package com.repositorio.mvp.domain.question.model;

import java.util.UUID;

import com.repositorio.mvp.domain.asset.model.Asset;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Table(name = "TB_QUESTION")
@Entity
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Builder
@AllArgsConstructor
public class Question {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Setter(AccessLevel.NONE)
    private UUID id;

    @NotBlank
    @Column(nullable = false, length = 100)
    private String question;

    @NotBlank
    @Column(nullable = false, length = 100)
    private String criterion;

    @NotNull
    @Column(nullable = false)
    private Integer quantity;

    @NotNull
    @Column(nullable = false)
    private Boolean response;
    
    @NotBlank
    @Column(nullable = false, length = 50)
    private String idQuestion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "active_id", nullable = false)
    @ToString.Exclude
    private Asset active;
}
