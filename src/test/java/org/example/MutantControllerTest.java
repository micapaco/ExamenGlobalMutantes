package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.dto.DnaRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Tests de integración para MutantController.
 * Total: 8 tests cubriendo todos los endpoints y casos.
 */
@SpringBootTest
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class MutantControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;

    // POST /mutant

    @Test
    @DisplayName("POST /mutant debe retornar 200 OK para ADN mutante")
    void whenMutantDna_thenReturn200() throws Exception {
        DnaRequest req = new DnaRequest(List.of(
                "ATGCGA",
                "CAGTGC",
                "TTATGT",
                "AGAAGG",
                "CCCCTA",
                "TCACTG"
        ));

        mockMvc.perform(post("/mutant")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /mutant debe retornar 403 Forbidden para ADN humano")
    void whenHumanDna_thenReturn403() throws Exception {
        DnaRequest req = new DnaRequest(List.of(
                "ATGCGA",
                "CAGTGC",
                "TTATTT",
                "AGACGG",
                "GCGTCA",
                "TCACTG"
        ));

        mockMvc.perform(post("/mutant")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("POST /mutant debe retornar 400 para ADN con caracteres inválidos")
    void whenInvalidCharacters_thenReturn400() throws Exception {
        DnaRequest req = new DnaRequest(List.of(
                "ATGCGA",
                "CAGTXC",
                "TTATGT",
                "AGAAGG",
                "CCCCTA",
                "TCACTG"
        ));

        mockMvc.perform(post("/mutant")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /mutant debe retornar 400 para matriz no cuadrada")
    void whenNonSquareMatrix_thenReturn400() throws Exception {
        DnaRequest req = new DnaRequest(List.of(
                "ATGCGA",
                "CAGTGC",
                "TTATGT"
        ));

        mockMvc.perform(post("/mutant")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /mutant debe retornar 400 para ADN vacío")
    void whenEmptyDna_thenReturn400() throws Exception {
        DnaRequest req = new DnaRequest(List.of());

        mockMvc.perform(post("/mutant")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /mutant debe retornar 400 para body vacío")
    void whenEmptyBody_thenReturn400() throws Exception {
        mockMvc.perform(post("/mutant")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(""))
                .andExpect(status().isBadRequest());
    }

    // GET /stats

    @Test
    @DisplayName("GET /stats debe retornar JSON con campos correctos")
    void whenGetStats_thenReturnJsonWithFields() throws Exception {
        mockMvc.perform(get("/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count_mutant_dna").exists())
                .andExpect(jsonPath("$.count_human_dna").exists())
                .andExpect(jsonPath("$.ratio").exists());
    }

    @Test
    @DisplayName("GET /stats debe calcular ratio correctamente después de análisis")
    void whenGetStatsAfterAnalysis_thenReturnCorrectRatio() throws Exception {
        // Primero enviamos un mutante
        DnaRequest mutantReq = new DnaRequest(List.of(
                "ATGCGA",
                "CAGTGC",
                "TTATGT",
                "AGAAGG",
                "CCCCTA",
                "TCACTG"
        ));
        mockMvc.perform(post("/mutant")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(mutantReq)))
                .andExpect(status().isOk());

        // Luego enviamos un humano
        DnaRequest humanReq = new DnaRequest(List.of(
                "ATGCGA",
                "CAGTGC",
                "TTATTT",
                "AGACGG",
                "GCGTCA",
                "TCACTG"
        ));
        mockMvc.perform(post("/mutant")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(humanReq)))
                .andExpect(status().isForbidden());

        // Verificamos las estadísticas
        mockMvc.perform(get("/stats"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count_mutant_dna").value(1))
                .andExpect(jsonPath("$.count_human_dna").value(1))
                .andExpect(jsonPath("$.ratio").value(1.0));
    }
}
