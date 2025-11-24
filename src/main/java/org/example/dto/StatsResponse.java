package org.example.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * DTO para la respuesta del endpoint GET /stats.
 * Contiene las estadísticas de verificaciones de ADN.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Estadísticas de verificaciones de ADN")
public class StatsResponse {

    @JsonProperty("count_mutant_dna")
    @Schema(
            description = "Cantidad de ADN mutante verificado",
            example = "40"
    )
    private long countMutantDna;

    @JsonProperty("count_human_dna")
    @Schema(
            description = "Cantidad de ADN humano verificado",
            example = "100"
    )
    private long countHumanDna;

    @Schema(
            description = "Ratio de mutantes sobre humanos (count_mutant_dna / count_human_dna)",
            example = "0.4"
    )
    private double ratio;
}
