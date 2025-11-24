package org.example.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.example.validation.ValidDnaSequence;

import java.util.List;

/**
 * DTO para el request del endpoint POST /mutant.
 * Contiene la secuencia de ADN a analizar.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request para verificar si un ADN es mutante")
public class DnaRequest {

    @NotNull(message = "El ADN no puede ser nulo")
    @NotEmpty(message = "El ADN no puede estar vacío")
    @ValidDnaSequence
    @Schema(
            description = "Secuencia de ADN representada como matriz NxN. " +
                    "Cada string representa una fila de la matriz. " +
                    "Solo se permiten los caracteres A, T, C, G.",
            example = "[\"ATGCGA\", \"CAGTGC\", \"TTATGT\", \"AGAAGG\", \"CCCCTA\", \"TCACTG\"]",
            required = true
    )
    private List<String> dna;
}
