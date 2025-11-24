package org.example.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.example.dto.DnaRequest;
import org.example.dto.StatsResponse;
import org.example.service.MutantService;
import org.example.service.StatsService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

/**
 * Controlador REST que expone los endpoints para detección de mutantes.
 */
@RestController
@RequestMapping
@RequiredArgsConstructor
@Tag(name = "Mutant Detector", description = "API para detección de mutantes mediante análisis de ADN")
public class MutantController {

    private final MutantService mutantService;
    private final StatsService statsService;

    /**
     * Endpoint para verificar si un ADN pertenece a un mutante.
     *
     * @param request Objeto con la secuencia de ADN a analizar
     * @return 200 OK si es mutante, 403 Forbidden si es humano
     */
    @PostMapping("/mutant")
    @Operation(
            summary = "Verificar si un ADN es mutante",
            description = "Recibe una secuencia de ADN y determina si pertenece a un mutante. " +
                    "Un mutante tiene más de una secuencia de 4 letras iguales consecutivas " +
                    "(horizontal, vertical o diagonal)."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "El ADN pertenece a un mutante",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "El ADN pertenece a un humano (no es mutante)",
                    content = @Content
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "ADN inválido (matriz no cuadrada, caracteres inválidos, etc.)",
                    content = @Content(schema = @Schema(implementation = Object.class))
            )
    })
    public ResponseEntity<Void> isMutant(@Valid @RequestBody DnaRequest request) {
        boolean isMutant = mutantService.processDna(request.getDna());

        if (isMutant) {
            return ResponseEntity.ok().build();
        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
    }

    /**
     * Endpoint para obtener estadísticas de verificaciones de ADN.
     *
     * @return Objeto con cantidad de mutantes, humanos y ratio
     */
    @GetMapping("/stats")
    @Operation(
            summary = "Obtener estadísticas de verificaciones",
            description = "Retorna la cantidad de ADN mutante verificado, ADN humano verificado " +
                    "y el ratio entre ambos (mutantes/humanos)."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Estadísticas obtenidas exitosamente",
                    content = @Content(schema = @Schema(implementation = StatsResponse.class))
            )
    })
    public ResponseEntity<StatsResponse> stats() {
        StatsResponse stats = statsService.getStats();
        return ResponseEntity.ok(stats);
    }
}
