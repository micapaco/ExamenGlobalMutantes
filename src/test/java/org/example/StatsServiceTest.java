package org.example;

import org.example.dto.StatsResponse;
import org.example.repository.DnaRecordRepository;
import org.example.service.StatsService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios con mocks para StatsService.
 * Total: 6 tests cubriendo todos los casos de estadísticas.
 */
@ExtendWith(MockitoExtension.class)
class StatsServiceTest {

    @Mock
    private DnaRecordRepository repository;

    @InjectMocks
    private StatsService service;

    @Test
    @DisplayName("Debe calcular estadísticas correctamente")
    void testStatsCalculation() {
        when(repository.countByMutantTrue()).thenReturn(40L);
        when(repository.countByMutantFalse()).thenReturn(100L);

        StatsResponse stats = service.getStats();

        assertEquals(40L, stats.getCountMutantDna());
        assertEquals(100L, stats.getCountHumanDna());
        assertEquals(0.4, stats.getRatio(), 0.001);
    }

    @Test
    @DisplayName("Debe retornar ratio 0 cuando no hay humanos")
    void testStatsWhenNoHumans() {
        when(repository.countByMutantTrue()).thenReturn(10L);
        when(repository.countByMutantFalse()).thenReturn(0L);

        StatsResponse stats = service.getStats();

        assertEquals(10L, stats.getCountMutantDna());
        assertEquals(0L, stats.getCountHumanDna());
        assertEquals(0.0, stats.getRatio(), 0.001);
    }

    @Test
    @DisplayName("Debe retornar ratio 0 cuando no hay datos")
    void testStatsWithNoData() {
        when(repository.countByMutantTrue()).thenReturn(0L);
        when(repository.countByMutantFalse()).thenReturn(0L);

        StatsResponse stats = service.getStats();

        assertEquals(0L, stats.getCountMutantDna());
        assertEquals(0L, stats.getCountHumanDna());
        assertEquals(0.0, stats.getRatio(), 0.001);
    }

    @Test
    @DisplayName("Debe calcular ratio con decimales correctamente")
    void testStatsWithDecimalRatio() {
        when(repository.countByMutantTrue()).thenReturn(1L);
        when(repository.countByMutantFalse()).thenReturn(3L);

        StatsResponse stats = service.getStats();

        assertEquals(1L, stats.getCountMutantDna());
        assertEquals(3L, stats.getCountHumanDna());
        assertEquals(0.333, stats.getRatio(), 0.001);
    }

    @Test
    @DisplayName("Debe retornar ratio 1.0 cuando hay igual cantidad")
    void testStatsWithEqualCounts() {
        when(repository.countByMutantTrue()).thenReturn(50L);
        when(repository.countByMutantFalse()).thenReturn(50L);

        StatsResponse stats = service.getStats();

        assertEquals(50L, stats.getCountMutantDna());
        assertEquals(50L, stats.getCountHumanDna());
        assertEquals(1.0, stats.getRatio(), 0.001);
    }

    @Test
    @DisplayName("Debe manejar grandes cantidades de datos")
    void testStatsWithLargeNumbers() {
        when(repository.countByMutantTrue()).thenReturn(1000000L);
        when(repository.countByMutantFalse()).thenReturn(2000000L);

        StatsResponse stats = service.getStats();

        assertEquals(1000000L, stats.getCountMutantDna());
        assertEquals(2000000L, stats.getCountHumanDna());
        assertEquals(0.5, stats.getRatio(), 0.001);
    }
}
