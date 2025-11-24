package org.example;

import org.example.entity.DnaRecord;
import org.example.repository.DnaRecordRepository;
import org.example.service.MutantDetector;
import org.example.service.MutantService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Tests unitarios con mocks para MutantService.
 * Total: 5 tests cubriendo la lógica de negocio.
 */
@ExtendWith(MockitoExtension.class)
class MutantServiceTest {

    @Mock
    private MutantDetector detector;

    @Mock
    private DnaRecordRepository repository;

    @InjectMocks
    private MutantService service;

    private List<String> mutantDna;
    private List<String> humanDna;

    @BeforeEach
    void setUp() {
        mutantDna = List.of("ATGCGA", "CAGTGC", "TTATGT", "AGAAGG", "CCCCTA", "TCACTG");
        humanDna = List.of("ATGCGA", "CAGTGC", "TTATTT", "AGACGG", "GCGTCA", "TCACTG");
    }

    @Test
    @DisplayName("Debe retornar resultado cacheado si el ADN ya fue analizado")
    void whenDnaAlreadyExists_returnItsStoredResult() {
        DnaRecord record = new DnaRecord();
        record.setMutant(true);

        when(repository.findByDnaHash(anyString())).thenReturn(Optional.of(record));

        boolean result = service.processDna(mutantDna);

        assertTrue(result);
        verify(detector, never()).isMutant(any());
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Debe analizar y guardar ADN mutante nuevo")
    void whenNewMutantDna_storeIt() {
        when(repository.findByDnaHash(anyString())).thenReturn(Optional.empty());
        when(detector.isMutant(mutantDna)).thenReturn(true);

        boolean result = service.processDna(mutantDna);

        assertTrue(result);
        verify(detector, times(1)).isMutant(mutantDna);
        verify(repository, times(1)).save(any(DnaRecord.class));
    }

    @Test
    @DisplayName("Debe analizar y guardar ADN humano nuevo")
    void whenNewHumanDna_storeIt() {
        when(repository.findByDnaHash(anyString())).thenReturn(Optional.empty());
        when(detector.isMutant(humanDna)).thenReturn(false);

        boolean result = service.processDna(humanDna);

        assertFalse(result);
        verify(detector, times(1)).isMutant(humanDna);
        verify(repository, times(1)).save(any(DnaRecord.class));
    }

    @Test
    @DisplayName("Debe generar hash consistente para el mismo ADN")
    void testConsistentHashGeneration() {
        when(repository.findByDnaHash(anyString())).thenReturn(Optional.empty());
        when(detector.isMutant(any())).thenReturn(true);

        ArgumentCaptor<String> hashCaptor = ArgumentCaptor.forClass(String.class);

        service.processDna(mutantDna);
        verify(repository).findByDnaHash(hashCaptor.capture());
        String firstHash = hashCaptor.getValue();

        reset(repository);
        when(repository.findByDnaHash(anyString())).thenReturn(Optional.empty());

        service.processDna(mutantDna);
        verify(repository).findByDnaHash(hashCaptor.capture());
        String secondHash = hashCaptor.getValue();

        assertEquals(firstHash, secondHash, "El mismo ADN debe generar el mismo hash");
    }

    @Test
    @DisplayName("Debe guardar registro con campos correctos")
    void testSavesRecordWithCorrectFields() {
        when(repository.findByDnaHash(anyString())).thenReturn(Optional.empty());
        when(detector.isMutant(mutantDna)).thenReturn(true);

        service.processDna(mutantDna);

        ArgumentCaptor<DnaRecord> recordCaptor = ArgumentCaptor.forClass(DnaRecord.class);
        verify(repository).save(recordCaptor.capture());

        DnaRecord savedRecord = recordCaptor.getValue();
        assertNotNull(savedRecord.getDnaHash());
        assertEquals(64, savedRecord.getDnaHash().length(), "Hash SHA-256 debe tener 64 caracteres");
        assertTrue(savedRecord.isMutant());
    }
}
