package com.sanosysalvos.matchservice.controller;

import com.sanosysalvos.matchservice.model.Match;
import com.sanosysalvos.matchservice.service.MatchingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;
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
    void health_ShouldReturnOk() throws Exception {
        mockMvc.perform(get("/api/matching/health"))
            .andExpect(status().isOk())
            .andExpect(content().string("Match Service is running"));
    }

    @Test
    void getAllMatches_ShouldReturnList() throws Exception {
        when(matchingService.getAllMatches()).thenReturn(List.of());
        mockMvc.perform(get("/api/matching"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(0));
    }

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
    void createMatch_ShouldReturn201() throws Exception {
        Match match = new Match();
        match.setId(1L);
        when(matchingService.createMatch(1L, 2L)).thenReturn(match);

        mockMvc.perform(post("/api/matching")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"petLostId\":1,\"petFoundId\":2}"))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void createMatch_ShouldReturn400WhenMissingIds() throws Exception {
        mockMvc.perform(post("/api/matching")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
            .andExpect(status().isBadRequest());
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
    void updateMatchStatus_ShouldReturn400WhenMissingStatus() throws Exception {
        mockMvc.perform(put("/api/matching/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void updateMatchStatus_ShouldReturn404WhenNotFound() throws Exception {
        when(matchingService.updateMatchStatus(99L, "CONFIRMED"))
            .thenThrow(new RuntimeException("Match not found with id: 99"));

        mockMvc.perform(put("/api/matching/99")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\":\"CONFIRMED\"}"))
            .andExpect(status().isNotFound());
    }

    @Test
    void deleteMatch_ShouldReturn204() throws Exception {
        mockMvc.perform(delete("/api/matching/1"))
            .andExpect(status().isNoContent());
    }

    @Test
    void deleteMatch_ShouldReturn404WhenNotFound() throws Exception {
        doThrow(new RuntimeException("Match not found with id: 99"))
            .when(matchingService).deleteMatch(99L);

        mockMvc.perform(delete("/api/matching/99"))
            .andExpect(status().isNotFound());
    }

    @Test
    void getMatchesByStatus_ShouldReturnFiltered() throws Exception {
        when(matchingService.getMatchesByStatus("PENDIENTE")).thenReturn(List.of(new Match(), new Match()));
        mockMvc.perform(get("/api/matching/search/status/PENDIENTE"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    void getMatchesByPercentage_ShouldReturnFiltered() throws Exception {
        when(matchingService.getMatchesByPercentage(80)).thenReturn(List.of(new Match()));
        mockMvc.perform(get("/api/matching/search/percentage/80"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getTotalsByStatus_ShouldReturnCounts() throws Exception {
        when(matchingService.countMatchesByStatus("PENDING")).thenReturn(5L);
        when(matchingService.countMatchesByStatus("CONFIRMED")).thenReturn(3L);
        when(matchingService.countMatchesByStatus("REJECTED")).thenReturn(1L);

        mockMvc.perform(get("/api/matching/totals/status"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.pending").value(5))
            .andExpect(jsonPath("$.confirmed").value(3))
            .andExpect(jsonPath("$.rejected").value(1));
    }

    @Test
    void runAutomaticMatching_ShouldReturnOk() throws Exception {
        mockMvc.perform(post("/api/matching/run-automatic"))
            .andExpect(status().isOk())
            .andExpect(content().string("Automatic matching completed"));
    }
}
