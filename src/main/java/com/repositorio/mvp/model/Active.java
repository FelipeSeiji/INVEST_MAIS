package com.repositorio.mvp.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.repositorio.mvp.enums.CategoryActive;

import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToMany;
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

@Entity
@Table(name = "TB_ACTIVE")
@Getter
@Setter
@ToString
@RequiredArgsConstructor
@Builder
@AllArgsConstructor
public class Active {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Setter(AccessLevel.NONE)
    private UUID id;

    @NotBlank
    @Column(nullable = false, length = 50)
    private String name;

    @NotNull
    @Column(nullable = false)
    private Integer amount;

    @NotNull
    @Column(nullable = false)
    private Double currentValue;
    
    @NotNull
    @Column(nullable = false)
    private Double note;
    
    @NotNull
    @Column(nullable = false)
    private Double price;
    
    @NotNull
    @Column(nullable = false)
    private Double recommend;
    
    @NotNull
    @Column(nullable = false)
    private Double percentage;
    
    @NotNull
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CategoryActive categoryActive;

    //Muitos ativos pertencem a um usuario
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude
    private User user;

    //Um ativo possui varias respostas
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "active_id")
    @ToString.Exclude
    private List<Question> answers = new ArrayList<>();
}
