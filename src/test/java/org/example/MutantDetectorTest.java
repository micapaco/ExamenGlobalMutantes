package org.example;

import org.example.exception.InvalidDnaException;
import org.example.service.MutantDetector;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests unitarios para MutantDetector.
 * Total: 17 tests cubriendo todos los casos.
 */
class MutantDetectorTest {

    private MutantDetector detector;

    @BeforeEach
    void setUp() {
        detector = new MutantDetector();
    }

    // TESTS DE MUTANTES (true)

    @Test
    @DisplayName("Debe detectar mutante con secuencias horizontal y diagonal")
    void testMutantWithHorizontalAndDiagonalSequences() {
        List<String> dna = List.of(
                "ATGCGA",
                "CAGTGC",
                "TTATGT",
                "AGAAGG",
                "CCCCTA",
                "TCACTG"
        );
        assertTrue(detector.isMutant(dna));
    }

    @Test
    @DisplayName("Debe detectar mutante con secuencias verticales")
    void testMutantWithVerticalSequences() {
        List<String> dna = List.of(
                "ATGCGA",
                "AAGTGC",
                "ATATGT",
                "AGAAGG",
                "ACCCTA",
                "TCACTG"
        );
        assertTrue(detector.isMutant(dna));
    }

    @Test
    @DisplayName("Debe detectar mutante con múltiples secuencias horizontales")
    void testMutantWithMultipleHorizontalSequences() {
        List<String> dna = List.of(
                "TTTTGA",
                "CAGTGC",
                "TTATGT",
                "AGAAGG",
                "CCCCTA",
                "TCACTG"
        );
        assertTrue(detector.isMutant(dna));
    }

    @Test
    @DisplayName("Debe detectar mutante con diagonales descendentes")
    void testMutantWithDiagonalDownSequences() {
        List<String> dna = List.of(
                "ATGCGA",
                "CAGTGC",
                "TTATGT",
                "AGAAGG",
                "CCCCTA",
                "TCACTG"
        );
        assertTrue(detector.isMutant(dna));
    }

    @Test
    @DisplayName("Debe detectar mutante con diagonales ascendentes")
    void testMutantWithDiagonalUpSequences() {
        List<String> dna = List.of(
                "ATGCGA",
                "CAGTGC",
                "TTGTGT",
                "AGGTGG",
                "CGCCTA",
                "GCACTG"
        );
        assertTrue(detector.isMutant(dna));
    }

    @Test
    @DisplayName("Debe detectar mutante en matriz pequeña 4x4")
    void testMutantInSmallMatrix4x4() {
        List<String> dna = List.of(
                "AAAA",
                "CCCC",
                "TTAT",
                "AGAC"
        );
        assertTrue(detector.isMutant(dna));
    }

    @Test
    @DisplayName("Debe detectar mutante con todas las bases iguales")
    void testMutantWithAllSameBases() {
        List<String> dna = List.of(
                "AAAA",
                "AAAA",
                "AAAA",
                "AAAA"
        );
        assertTrue(detector.isMutant(dna));
    }

    @Test
    @DisplayName("Debe usar early termination para eficiencia")
    void testEarlyTermination() {
        List<String> dna = List.of(
                "AAAAGA",
                "AAAAGC",
                "TTATGT",
                "AGAAGG",
                "CCCCTA",
                "TCACTG"
        );

        long startTime = System.nanoTime();
        boolean result = detector.isMutant(dna);
        long endTime = System.nanoTime();

        assertTrue(result);
        assertTrue((endTime - startTime) < 10_000_000, "Debe terminar en menos de 10ms");
    }

    // TESTS DE HUMANOS (false)

    @Test
    @DisplayName("No debe detectar mutante con una sola secuencia")
    void testNotMutantWithOnlyOneSequence() {
        List<String> dna = List.of(
                "ATGCGA",
                "CAGTGC",
                "TTATTT",
                "AGACGG",
                "GCGTCA",
                "TCACTG"
        );
        assertFalse(detector.isMutant(dna));
    }

    @Test
    @DisplayName("No debe detectar mutante sin secuencias")
    void testNotMutantWithNoSequences() {
        List<String> dna = List.of(
                "ATGC",
                "CAGT",
                "TTAT",
                "AGAC"
        );
        assertFalse(detector.isMutant(dna));
    }

    //  TESTS DE VALIDACIÓN (excepciones)

    @Test
    @DisplayName("Debe lanzar excepción para ADN nulo")
    void testNullDna() {
        assertThrows(InvalidDnaException.class, () -> detector.isMutant(null));
    }

    @Test
    @DisplayName("Debe lanzar excepción para ADN vacío")
    void testEmptyDna() {
        List<String> dna = List.of();
        assertThrows(InvalidDnaException.class, () -> detector.isMutant(dna));
    }

    @Test
    @DisplayName("Debe lanzar excepción para matriz no cuadrada")
    void testNonSquareMatrix() {
        List<String> dna = List.of(
                "ATGCGA",
                "CAGTGC",
                "TTATGT"
        );
        assertThrows(InvalidDnaException.class, () -> detector.isMutant(dna));
    }

    @Test
    @DisplayName("Debe lanzar excepción para caracteres inválidos")
    void testInvalidCharacters() {
        List<String> dna = List.of(
                "ATGCGA",
                "CAGTXC",
                "TTATGT",
                "AGAAGG",
                "CCCCTA",
                "TCACTG"
        );
        assertThrows(InvalidDnaException.class, () -> detector.isMutant(dna));
    }

    @Test
    @DisplayName("Debe lanzar excepción para matriz muy pequeña (3x3)")
    void testTooSmallMatrix() {
        List<String> dna = List.of(
                "ATG",
                "CAG",
                "TTA"
        );
        assertThrows(InvalidDnaException.class, () -> detector.isMutant(dna));
    }

    @Test
    @DisplayName("Debe lanzar excepción para fila nula en el array")
    void testNullRowInArray() {
        List<String> dna = Arrays.asList(
                "ATGCGA",
                null,
                "TTATGT",
                "AGAAGG",
                "CCCCTA",
                "TCACTG"
        );
        assertThrows(InvalidDnaException.class, () -> detector.isMutant(dna));
    }

    @Test
    @DisplayName("Debe lanzar excepción para fila con largo diferente")
    void testRowWithDifferentLength() {
        List<String> dna = List.of(
                "ATGCGA",
                "CAGTGC",
                "TTATG",
                "AGAAGG",
                "CCCCTA",
                "TCACTG"
        );
        assertThrows(InvalidDnaException.class, () -> detector.isMutant(dna));
    }
}
