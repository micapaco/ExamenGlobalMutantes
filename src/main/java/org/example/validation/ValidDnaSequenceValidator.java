package org.example.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.List;
import java.util.Set;

/**
 * Implementación del validador para secuencias de ADN.
 *
 * Valida que la secuencia de ADN cumpla con los requisitos:
 * - No sea null ni vacía
 * - Sea una matriz cuadrada NxN
 * - Tenga tamaño mínimo 4x4
 * - Solo contenga caracteres A, T, C, G
 */
public class ValidDnaSequenceValidator implements ConstraintValidator<ValidDnaSequence, List<String>> {

    private static final Set<Character> VALID_BASES = Set.of('A', 'T', 'C', 'G');
    private static final int MIN_SIZE = 4;

    @Override
    public void initialize(ValidDnaSequence constraintAnnotation) {
        // No initialization needed
    }

    @Override
    public boolean isValid(List<String> dna, ConstraintValidatorContext context) {
        // Null o vacío es inválido
        if (dna == null || dna.isEmpty()) {
            return false;
        }

        int n = dna.size();

        // Tamaño mínimo 4x4
        if (n < MIN_SIZE) {
            return false;
        }

        // Validar cada fila
        for (String row : dna) {
            // Fila null es inválida
            if (row == null) {
                return false;
            }

            // Debe ser matriz cuadrada NxN
            if (row.length() != n) {
                return false;
            }

            // Validar que solo contenga caracteres válidos
            for (char c : row.toCharArray()) {
                if (!VALID_BASES.contains(c)) {
                    return false;
                }
            }
        }

        return true;
    }
}
