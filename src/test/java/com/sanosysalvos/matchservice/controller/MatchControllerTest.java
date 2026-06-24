package com.sanosysalvos.matchservice.controller;

import com.sanosysalvos.matchservice.model.Match;
import com.sanosysalvos.matchservice.service.MatchingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MatchController.class)
class MatchControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private MatchingService matchingService;

    @Test
    void getMatchById_ShouldReturnMatch() throws Exception {
        Match match = new Match();
        match.setId(1L);
        when(matchingService.getMatchById(1L)).thenReturn(Optional.of(match));

        mockMvc.perform(get("/api/matching/1"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void getMatchById_ShouldReturn404WhenNotFound() throws Exception {
        when(matchingService.getMatchById(99L)).thenReturn(Optional.empty());
        mockMvc.perform(get("/api/matching/99"))
            .andExpect(status().isNotFound());
    }

    @Test
    void updateMatchStatus_ShouldReturnOk() throws Exception {
        Match match = new Match();
        match.setId(1L);
        match.setEstado("CONFIRMED");
        when(matchingService.updateMatchStatus(1L, "CONFIRMED")).thenReturn(match);

        mockMvc.perform(put("/api/matching/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\":\"CONFIRMED\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.estado").value("CONFIRMED"));
    }

    @Test
    void getTotalsByStatus_ShouldReturnCounts() throws Exception {
        when(matchingService.countMatchesByStatus("PENDIENTE")).thenReturn(5L);
        when(matchingService.countMatchesByStatus("CONFIRMED")).thenReturn(3L);
        when(matchingService.countMatchesByStatus("REJECTED")).thenReturn(1L);

        mockMvc.perform(get("/api/matching/totals/status"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.pending").value(5))
            .andExpect(jsonPath("$.confirmed").value(3))
            .andExpect(jsonPath("$.rejected").value(1));
    }

    @Test
    void createMatch_ShouldReturn400WhenMissingIds() throws Exception {
        mockMvc.perform(post("/api/matching")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
            .andExpect(status().isBadRequest());
    }
}
