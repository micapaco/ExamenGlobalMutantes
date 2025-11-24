package org.example.repository;

import org.example.entity.DnaRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

//Repositorio JPA para acceder a la tabla dna_records
public interface DnaRecordRepository extends JpaRepository<DnaRecord, Long> {

    Optional<DnaRecord> findByDnaHash(String dnaHash);

    long countByMutantTrue();

    long countByMutantFalse();
}
