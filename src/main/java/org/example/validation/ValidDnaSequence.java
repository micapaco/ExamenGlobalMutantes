package org.example.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Anotación de validación personalizada para secuencias de ADN.
 *
 * Valida que:
 * - La lista no sea null ni vacía
 * - La matriz sea cuadrada NxN
 * - El tamaño mínimo sea 4x4
 * - Solo contenga caracteres válidos (A, T, C, G)
 */
@Documented
@Constraint(validatedBy = ValidDnaSequenceValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidDnaSequence {

    String message() default "Invalid DNA sequence: must be a square NxN matrix (minimum 4x4) with only A, T, C, G characters";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
