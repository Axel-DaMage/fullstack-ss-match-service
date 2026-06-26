package com.sanosysalvos.matchservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sanosysalvos.matchservice.model.Match;
import com.sanosysalvos.matchservice.service.MatchingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MatchController.class)
class MatchControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private MatchingService serviceMock;

    private final ObjectMapper mapper = new ObjectMapper();

    private Match crearMatch(Long id, String estado) {
        Match m = new Match();
        m.setId(id);
        m.setMascotaPerdidaId(1L);
        m.setMascotaEncontradaId(2L);
        m.setEstado(estado);
        m.setPorcentajeCoincidencia(85);
        return m;
    }

    @Test
    void health_DeberiaRetornar200() throws Exception {
        mvc.perform(get("/api/matching/health"))
                .andExpect(status().isOk())
                .andExpect(content().string("Match Service is running"));
    }

    @Test
    void obtenerTodos_DeberiaRetornarLista() throws Exception {
        when(serviceMock.getAllMatches()).thenReturn(List.of(crearMatch(1L, "PENDIENTE")));

        mvc.perform(get("/api/matching"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void obtenerPorId_CuandoExiste_DeberiaRetornar200() throws Exception {
        when(serviceMock.getMatchById(1L)).thenReturn(Optional.of(crearMatch(1L, "PENDIENTE")));

        mvc.perform(get("/api/matching/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("PENDIENTE"));
    }

    @Test
    void obtenerPorId_CuandoNoExiste_DeberiaRetornar404() throws Exception {
        when(serviceMock.getMatchById(99L)).thenReturn(Optional.empty());

        mvc.perform(get("/api/matching/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void crearMatch_ConIdsValidos_DeberiaRetornar201() throws Exception {
        when(serviceMock.createMatch(1L, 2L)).thenReturn(crearMatch(1L, "PENDIENTE"));

        mvc.perform(post("/api/matching")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"petLostId\":1,\"petFoundId\":2}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void crearMatch_SinIds_DeberiaRetornar400() throws Exception {
        mvc.perform(post("/api/matching")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void actualizarEstado_ConStatusValido_DeberiaRetornar200() throws Exception {
        when(serviceMock.updateMatchStatus(1L, "CONFIRMED")).thenReturn(crearMatch(1L, "CONFIRMED"));

        mvc.perform(put("/api/matching/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"CONFIRMED\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("CONFIRMED"));
    }

    @Test
    void actualizarEstado_SinStatus_DeberiaRetornar400() throws Exception {
        mvc.perform(put("/api/matching/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void actualizarEstado_CuandoNoExiste_DeberiaRetornar404() throws Exception {
        when(serviceMock.updateMatchStatus(eq(99L), anyString()))
                .thenThrow(new RuntimeException("not found"));

        mvc.perform(put("/api/matching/99")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\":\"CONFIRMED\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void eliminarMatch_CuandoExiste_DeberiaRetornar204() throws Exception {
        mvc.perform(delete("/api/matching/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void eliminarMatch_CuandoNoExiste_DeberiaRetornar404() throws Exception {
        doThrow(new RuntimeException("not found")).when(serviceMock).deleteMatch(99L);

        mvc.perform(delete("/api/matching/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    void buscarPorStatus_DeberiaRetornar200() throws Exception {
        when(serviceMock.getMatchesByStatus("PENDIENTE"))
                .thenReturn(List.of(crearMatch(1L, "PENDIENTE")));

        mvc.perform(get("/api/matching/search/status/PENDIENTE"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].estado").value("PENDIENTE"));
    }

    @Test
    void buscarPorPorcentaje_DeberiaRetornar200() throws Exception {
        when(serviceMock.getMatchesByPercentage(80))
                .thenReturn(List.of(crearMatch(1L, "PENDIENTE")));

        mvc.perform(get("/api/matching/search/percentage/80"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].porcentajeCoincidencia").value(85));
    }

    @Test
    void totalesPorEstado_DeberiaRetornarConteos() throws Exception {
        when(serviceMock.countMatchesByStatus("PENDIENTE")).thenReturn(5L);
        when(serviceMock.countMatchesByStatus("CONFIRMED")).thenReturn(3L);
        when(serviceMock.countMatchesByStatus("REJECTED")).thenReturn(1L);

        mvc.perform(get("/api/matching/totals/status"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.pending").value(5))
                .andExpect(jsonPath("$.confirmed").value(3))
                .andExpect(jsonPath("$.rejected").value(1));
    }

    @Test
    void ejecutarMatchingAutomatico_DeberiaRetornar200() throws Exception {
        mvc.perform(post("/api/matching/run-automatic"))
                .andExpect(status().isOk())
                .andExpect(content().string("Automatic matching completed"));
    }
}
