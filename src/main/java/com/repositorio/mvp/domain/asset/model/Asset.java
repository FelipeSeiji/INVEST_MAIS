package com.repositorio.mvp.domain.asset.model;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.hibernate.annotations.Formula;

import com.repositorio.mvp.domain.asset.model.enums.AssetCategory;
import com.repositorio.mvp.domain.question.model.Question;
import com.repositorio.mvp.domain.user.model.User;

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
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "TB_ASSET")
@Getter
@ToString
@RequiredArgsConstructor
@Builder
@AllArgsConstructor
public class Asset {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Setter(AccessLevel.NONE)
    private UUID id;

    @NotBlank
    @Column(nullable = false, length = 50)
    private String name;

    @NotNull
    @Column(nullable = false, precision = 19, scale = 8)
    @PositiveOrZero
    private BigDecimal amount;

    @NotNull
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal currentValue;
    
    @NotNull
    @Column(nullable = false, precision = 10, scale = 2)
    @PositiveOrZero
    private BigDecimal note;
    
    @NotNull
    @Column(nullable = false, precision = 19, scale = 2)
    @PositiveOrZero
    private BigDecimal price;
    
    @NotNull
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal recommend;
    
    @NotNull
    @Column(nullable = false, precision = 10, scale = 2)
    @PositiveOrZero
    private BigDecimal percentage;
    
    @NotNull
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AssetCategory categoryActive;

    @Formula("amount * price")
    private BigDecimal totalValue;

    //Muitos ativos pertencem a um usuario
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude
    private User user;

    //Um ativo possui varias respostas
    @Builder.Default
    @OneToMany(mappedBy = "active",cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    private List<Question> answers = new ArrayList<>();
}
