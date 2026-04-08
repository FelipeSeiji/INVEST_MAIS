package com.repositorio.mvp.common.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.repositorio.mvp.common.validation.constants.NameConstraintValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Documented
@Constraint(validatedBy = NameConstraintValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
@NotBlank(message = "O nome é obrigatório")
@Size(max = 50, message = "O nome não pode ter mais de 50 caracteres")
public @interface ValidName {
    String message() default "O nome deve conter apenas letras";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
