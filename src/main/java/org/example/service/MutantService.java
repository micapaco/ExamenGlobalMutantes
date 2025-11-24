package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.entity.DnaRecord;
import org.example.repository.DnaRecordRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor   // Lombok: genera constructor con los campos final
public class MutantService {

    // Dependencias: el detector y el repositorio
    private final MutantDetector mutantDetector;
    private final DnaRecordRepository dnaRecordRepository;

    /**
     * Procesa el ADN:
     * 1) Calcula el hash del ADN.
     * 2) Si el hash ya existe en la BD → reutiliza ese resultado.
     * 3) Si no existe → llama al detector, guarda el registro y devuelve el resultado.
     */
    @Transactional
    public boolean processDna(List<String> dna) {

        // 1) Calculamos el hash del ADN (huella digital única)
        String hash = hashDna(dna);

        // 2) Buscamos si ya existe ese ADN (por hash) en la base de datos
        Optional<DnaRecord> existing = dnaRecordRepository.findByDnaHash(hash);

        if (existing.isPresent()) {
            // Si ya lo analizamos antes, devolvemos el resultado guardado
            return existing.get().isMutant();
        }

        // 3) Si no existe en BD, llamamos al detector para saber si es mutante
        boolean isMutant = mutantDetector.isMutant(dna);

        // 4) Creamos y guardamos un nuevo registro
        DnaRecord record = DnaRecord.builder()
                .dnaHash(hash)
                .mutant(isMutant)
                .build();

        dnaRecordRepository.save(record);

        // 5) Devolvemos el resultado al controller
        return isMutant;
    }

    /**
     * Genera un hash SHA-256 a partir de la lista de ADN.
     *
     * Ejemplo:
     * ["ATGCGA", "CAGTGC"] → "ATGCGA-CAGTGC" → hash hexadecimal.
     */
    private String hashDna(List<String> dna) {
        String joined = String.join("-", dna);

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(joined.getBytes(StandardCharsets.UTF_8));

            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b));
            }

            return sb.toString();

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error al calcular hash del ADN", e);
        }
    }
}
