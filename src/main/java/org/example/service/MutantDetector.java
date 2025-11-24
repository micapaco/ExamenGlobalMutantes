package org.example.service;

import org.example.exception.InvalidDnaException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * Servicio que implementa el algoritmo de detección de mutantes.
 *
 * Un ADN es mutante si contiene MÁS DE UNA secuencia de 4 letras
 * iguales consecutivas en cualquier dirección (horizontal, vertical,
 * diagonal descendente o diagonal ascendente).
 */
@Service
public class MutantDetector {

    private static final int SEQUENCE_LENGTH = 4;
    private static final Set<Character> VALID_BASES = Set.of('A', 'T', 'C', 'G');

    /**
     * Método principal: valida el ADN y verifica si es mutante.
     *
     * @param dna Lista de strings representando la matriz de ADN
     * @return true si es mutante (>1 secuencia), false si es humano
     * @throws InvalidDnaException si el ADN es inválido
     */
    public boolean isMutant(List<String> dna) {
        // Validamos el ADN. Si es inválido, lanza InvalidDnaException → HTTP 400.
        validateDna(dna);

        int n = dna.size();

        // Conversión a char[][] para acceso O(1) más rápido
        char[][] matrix = new char[n][n];
        for (int i = 0; i < n; i++) {
            matrix[i] = dna.get(i).toCharArray();
        }

        int sequenceCount = 0;

        // Single Pass: recorremos la matriz una sola vez
        for (int row = 0; row < n; row++) {
            for (int col = 0; col < n; col++) {

                // Boundary Checking + Horizontal Check
                if (col <= n - SEQUENCE_LENGTH && checkHorizontal(matrix, row, col)) {
                    sequenceCount++;
                    if (sequenceCount > 1) return true;  // Early Termination
                }

                // Boundary Checking + Vertical Check
                if (row <= n - SEQUENCE_LENGTH && checkVertical(matrix, row, col)) {
                    sequenceCount++;
                    if (sequenceCount > 1) return true;  // Early Termination
                }

                // Boundary Checking + Diagonal Descendente Check (↘)
                if (row <= n - SEQUENCE_LENGTH && col <= n - SEQUENCE_LENGTH && checkDiagonalDown(matrix, row, col)) {
                    sequenceCount++;
                    if (sequenceCount > 1) return true;  // Early Termination
                }

                // Boundary Checking + Diagonal Ascendente Check (↗)
                if (row >= SEQUENCE_LENGTH - 1 && col <= n - SEQUENCE_LENGTH && checkDiagonalUp(matrix, row, col)) {
                    sequenceCount++;
                    if (sequenceCount > 1) return true;  // Early Termination
                }
            }
        }

        return false;  // Solo encontró 0 o 1 secuencia → No es mutante
    }

    /**
     * Valida que el ADN cumpla con los requisitos:
     * - No sea null ni vacío
     * - Sea una matriz cuadrada NxN
     * - Tenga tamaño mínimo 4x4
     * - Solo contenga caracteres válidos (A, T, C, G)
     *
     * @param dna Lista de strings a validar
     * @throws InvalidDnaException si alguna validación falla
     */
    private void validateDna(List<String> dna) {
        if (dna == null || dna.isEmpty()) {
            throw new InvalidDnaException("El ADN no puede ser nulo ni vacío");
        }

        int n = dna.size();

        // Tamaño mínimo 4x4
        if (n < SEQUENCE_LENGTH) {
            throw new InvalidDnaException("El ADN debe ser una matriz cuadrada mínima de 4x4");
        }

        for (String row : dna) {
            if (row == null) {
                throw new InvalidDnaException("Las filas de ADN no pueden ser nulas");
            }

            // Matriz NxN: cada fila debe tener largo = cantidad de filas
            if (row.length() != n) {
                throw new InvalidDnaException("El ADN debe ser una matriz cuadrada NxN");
            }

            // Validación O(1) con Set - Solo caracteres válidos
            for (char c : row.toCharArray()) {
                if (!VALID_BASES.contains(c)) {
                    throw new InvalidDnaException("El ADN solo puede contener las letras A, C, G y T");
                }
            }
        }
    }

    /**
     * Verifica si hay 4 letras iguales consecutivas en horizontal (→)
     * Comparación directa sin loops para máximo rendimiento.
     */
    private boolean checkHorizontal(char[][] matrix, int row, int col) {
        char base = matrix[row][col];
        return matrix[row][col + 1] == base &&
               matrix[row][col + 2] == base &&
               matrix[row][col + 3] == base;
    }

    /**
     * Verifica si hay 4 letras iguales consecutivas en vertical (↓)
     */
    private boolean checkVertical(char[][] matrix, int row, int col) {
        char base = matrix[row][col];
        return matrix[row + 1][col] == base &&
               matrix[row + 2][col] == base &&
               matrix[row + 3][col] == base;
    }

    /**
     * Verifica si hay 4 letras iguales consecutivas en diagonal descendente (↘)
     */
    private boolean checkDiagonalDown(char[][] matrix, int row, int col) {
        char base = matrix[row][col];
        return matrix[row + 1][col + 1] == base &&
               matrix[row + 2][col + 2] == base &&
               matrix[row + 3][col + 3] == base;
    }

    /**
     * Verifica si hay 4 letras iguales consecutivas en diagonal ascendente (↗)
     */
    private boolean checkDiagonalUp(char[][] matrix, int row, int col) {
        char base = matrix[row][col];
        return matrix[row - 1][col + 1] == base &&
               matrix[row - 2][col + 2] == base &&
               matrix[row - 3][col + 3] == base;
    }
}
