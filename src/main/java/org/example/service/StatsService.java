package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.StatsResponse;
import org.example.repository.DnaRecordRepository;
import org.springframework.stereotype.Service;

/**
 * Servicio encargado de obtener estadísticas de la base de datos.
 *
 * Es usado por el controller cuando se llama al endpoint GET /stats.
 */
@Service
@RequiredArgsConstructor
public class StatsService {

    /**
     * Repositorio para consultar la tabla dna_records.
     * Spring Boot inyecta esta dependencia automáticamente usando el constructor.
     */
    private final DnaRecordRepository dnaRecordRepository;

    /**
     * Obtiene las estadísticas:
     * - cantidad de ADN mutante
     * - cantidad de ADN humano
     * - ratio (mutantes / humanos)
     *
     * @return objeto StatsResponse con los valores calculados
     */
    public StatsResponse getStats() {

        // 1) Contamos cuántos registros son mutantes (mutant = true)
        long mutants = dnaRecordRepository.countByMutantTrue();

        // 2) Contamos cuántos registros son humanos (mutant = false)
        long humans = dnaRecordRepository.countByMutantFalse();

        // 3) Calculamos el ratio:
        //    - si no hay humanos (humans = 0) → ratio = 0 para evitar división por cero
        double ratio;
        if (humans == 0) {
            ratio = 0.0;
        } else {
            ratio = (double) mutants / humans;
        }

        // 4) Devolvemos un objeto StatsResponse con los 3 valores
        return new StatsResponse(mutants, humans, ratio);
    }
}
