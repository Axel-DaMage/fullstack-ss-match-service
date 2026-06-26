package com.sanosysalvos.matchservice.service;

import com.sanosysalvos.matchservice.client.LocationServiceClient;
import com.sanosysalvos.matchservice.client.PetServiceClient;
import com.sanosysalvos.matchservice.model.Match;
import com.sanosysalvos.matchservice.model.PetDto;
import com.sanosysalvos.matchservice.repository.MatchCriteriaRepository;
import com.sanosysalvos.matchservice.repository.MatchRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MatchingServiceTest {

    @Mock
    private MatchRepository matchRepo;
    @Mock
    private MatchCriteriaRepository criteriaRepo;
    @Mock
    private PetServiceClient petClient;
    @Mock
    private LocationServiceClient locationClient;

    private MatchingService service;

    private PetDto perdido;
    private PetDto encontrado;

    @BeforeEach
    void setUp() {
        service = new MatchingService(matchRepo, criteriaRepo, petClient, locationClient);

        perdido = new PetDto();
        perdido.setId(1L);
        perdido.setRace("Labrador");
        perdido.setColor("Marron");
        perdido.setSize("Grande");

        encontrado = new PetDto();
        encontrado.setId(2L);
        encontrado.setRace("Labrador");
        encontrado.setColor("Marron");
        encontrado.setSize("Grande");
    }

    @Test
    void obtenerTodos_DeberiaRetornarLista() {
        when(matchRepo.findAll()).thenReturn(List.of(new Match()));
        assertEquals(1, service.getAllMatches().size());
        verify(matchRepo).findAll();
    }

    @Test
    void obtenerPorId_CuandoExiste_DeberiaRetornarMatch() {
        Match m = new Match();
        m.setId(1L);
        when(matchRepo.findById(1L)).thenReturn(Optional.of(m));

        assertTrue(service.getMatchById(1L).isPresent());
        assertEquals(1L, service.getMatchById(1L).get().getId());
    }

    @Test
    void obtenerPorId_CuandoNoExiste_DeberiaRetornarVacio() {
        when(matchRepo.findById(99L)).thenReturn(Optional.empty());
        assertTrue(service.getMatchById(99L).isEmpty());
    }

    @Test
    void crearMatch100Porciento_MascotasIdenticas() {
        when(petClient.getPetById(1L)).thenReturn(perdido);
        when(petClient.getPetById(2L)).thenReturn(encontrado);
        when(matchRepo.save(any(Match.class))).thenAnswer(i -> {
            Match m = i.getArgument(0);
            m.setId(1L);
            return m;
        });
        when(criteriaRepo.saveAll(anyList())).thenReturn(List.of());

        Match resultado = service.createMatch(1L, 2L);
        assertEquals(100, resultado.getPorcentajeCoincidencia());
        assertEquals("PENDIENTE", resultado.getEstado());
    }

    @Test
    void crearMatch63Porciento_CoincidenciaParcial() {
        encontrado.setColor("Negro");
        encontrado.setSize("Pequeño");

        when(petClient.getPetById(1L)).thenReturn(perdido);
        when(petClient.getPetById(2L)).thenReturn(encontrado);
        when(matchRepo.save(any(Match.class))).thenAnswer(i -> {
            Match m = i.getArgument(0);
            m.setId(1L);
            return m;
        });
        when(criteriaRepo.saveAll(anyList())).thenReturn(List.of());

        Match resultado = service.createMatch(1L, 2L);
        assertEquals(63, resultado.getPorcentajeCoincidencia());
    }

    @Test
    void crearMatch40Porciento_SoloTamanoCoincide() {
        encontrado.setRace("Pastor");
        encontrado.setColor("Blanco");

        when(petClient.getPetById(1L)).thenReturn(perdido);
        when(petClient.getPetById(2L)).thenReturn(encontrado);
        when(matchRepo.save(any(Match.class))).thenAnswer(i -> {
            Match m = i.getArgument(0);
            m.setId(1L);
            return m;
        });
        when(criteriaRepo.saveAll(anyList())).thenReturn(List.of());

        Match resultado = service.createMatch(1L, 2L);
        assertEquals(40, resultado.getPorcentajeCoincidencia());
    }

    @Test
    void crearMatch_LanzaExcepcionSiMascotaNoExiste() {
        when(petClient.getPetById(1L)).thenReturn(null);
        assertThrows(RuntimeException.class, () -> service.createMatch(1L, 2L));
    }

    @Test
    void actualizarEstado_CuandoExiste_DeberiaCambiarEstado() {
        Match existente = new Match();
        existente.setId(1L);
        existente.setEstado("PENDIENTE");
        when(matchRepo.findById(1L)).thenReturn(Optional.of(existente));
        when(matchRepo.save(any(Match.class))).thenAnswer(i -> i.getArgument(0));

        Match resultado = service.updateMatchStatus(1L, "CONFIRMED");
        assertEquals("CONFIRMED", resultado.getEstado());
    }

    @Test
    void actualizarEstado_CuandoNoExiste_DeberiaLanzarExcepcion() {
        when(matchRepo.findById(99L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> service.updateMatchStatus(99L, "CONFIRMED"));
    }

    @Test
    void eliminarMatch_CuandoExiste_DeberiaEliminar() {
        Match m = new Match();
        m.setId(1L);
        when(matchRepo.findById(1L)).thenReturn(Optional.of(m));

        service.deleteMatch(1L);
        verify(matchRepo).delete(m);
    }

    @Test
    void eliminarMatch_CuandoNoExiste_DeberiaLanzarExcepcion() {
        when(matchRepo.findById(99L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> service.deleteMatch(99L));
    }

    @Test
    void buscarPorEstado_DeberiaRetornarFiltrados() {
        when(matchRepo.findByEstado("PENDIENTE")).thenReturn(List.of(new Match()));
        assertEquals(1, service.getMatchesByStatus("PENDIENTE").size());
        verify(matchRepo).findByEstado("PENDIENTE");
    }

    @Test
    void buscarPorPorcentaje_DeberiaRetornarFiltrados() {
        when(matchRepo.findByPorcentajeCoincidenciaGreaterThanEqual(80)).thenReturn(List.of(new Match()));
        assertEquals(1, service.getMatchesByPercentage(80).size());
        verify(matchRepo).findByPorcentajeCoincidenciaGreaterThanEqual(80);
    }

    @Test
    void contarPorEstado_DeberiaRetornarCantidad() {
        when(matchRepo.countByEstado("PENDIENTE")).thenReturn(3L);
        assertEquals(3L, service.countMatchesByStatus("PENDIENTE"));
    }

    @Test
    void matchingAutomatico_CreaMatchesSimilares() {
        when(petClient.getPetsByStatus("PERDIDO")).thenReturn(List.of(perdido));
        when(petClient.getPetsByStatus("ENCONTRADO")).thenReturn(List.of(encontrado));
        when(petClient.getPetById(1L)).thenReturn(perdido);
        when(petClient.getPetById(2L)).thenReturn(encontrado);
        when(matchRepo.save(any(Match.class))).thenReturn(new Match());
        when(criteriaRepo.saveAll(anyList())).thenReturn(List.of());

        service.runAutomaticMatching();
        verify(matchRepo, atLeastOnce()).save(any(Match.class));
    }

    @Test
    void matchingAutomatico_NoCreaMatchBajo60() {
        encontrado.setRace("Pastor");
        encontrado.setColor("Blanco");
        encontrado.setSize("Pequeño");

        when(petClient.getPetsByStatus("PERDIDO")).thenReturn(List.of(perdido));
        when(petClient.getPetsByStatus("ENCONTRADO")).thenReturn(List.of(encontrado));

        service.runAutomaticMatching();
        verify(matchRepo, never()).save(any(Match.class));
    }

    @Test
    void matchingAutomatico_SinMascotas_NoHaceNada() {
        when(petClient.getPetsByStatus("PERDIDO")).thenReturn(List.of());
        when(petClient.getPetsByStatus("ENCONTRADO")).thenReturn(List.of());

        service.runAutomaticMatching();
        verify(matchRepo, never()).save(any());
    }
}
