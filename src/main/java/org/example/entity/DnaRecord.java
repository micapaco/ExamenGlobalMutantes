package org.example.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Entidad JPA que representa un registro de ADN analizado.
 * Se almacena en la tabla 'dna_records'.
 */
@Entity
@Table(name = "dna_records", indexes = {
        @Index(name = "idx_dna_hash", columnList = "dna_hash"),
        @Index(name = "idx_is_mutant", columnList = "is_mutant")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DnaRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Hash SHA-256 del ADN. Se usa para identificar ADN duplicados
     * y evitar re-análisis innecesarios.
     */
    @Column(name = "dna_hash", nullable = false, unique = true, length = 64)
    private String dnaHash;

    /**
     * Indica si el ADN pertenece a un mutante (true) o humano (false).
     */
    @Column(name = "is_mutant", nullable = false)
    private boolean mutant;

    /**
     * Fecha y hora en que se realizó el análisis.
     * Se genera automáticamente al crear el registro.
     */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
