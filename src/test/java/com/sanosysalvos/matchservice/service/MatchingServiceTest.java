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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MatchingServiceTest {

    @Mock
    private MatchRepository matchRepository;
    @Mock
    private MatchCriteriaRepository matchCriteriaRepository;
    @Mock
    private PetServiceClient petServiceClient;
    @Mock
    private LocationServiceClient locationServiceClient;

    private MatchingService matchingService;

    @BeforeEach
    void setUp() {
        matchingService = new MatchingService(matchRepository, matchCriteriaRepository, petServiceClient, locationServiceClient);
    }

    @Test
    void creaMatch100PorcientoMascotasIdenticas() {
        PetDto lostPet = new PetDto();
        lostPet.setId(1L);
        lostPet.setRace("Labrador");
        lostPet.setColor("Marron");
        lostPet.setSize("Grande");

        PetDto foundPet = new PetDto();
        foundPet.setId(2L);
        foundPet.setRace("Labrador");
        foundPet.setColor("Marron");
        foundPet.setSize("Grande");

        when(petServiceClient.getPetById(1L)).thenReturn(lostPet);
        when(petServiceClient.getPetById(2L)).thenReturn(foundPet);
        when(matchRepository.save(any(Match.class))).thenAnswer(i -> {
            Match m = i.getArgument(0);
            m.setId(1L);
            return m;
        });
        when(matchCriteriaRepository.saveAll(anyList())).thenReturn(List.of());

        Match result = matchingService.createMatch(1L, 2L);
        assertEquals(100, result.getPorcentajeCoincidencia());
        assertEquals("PENDIENTE", result.getEstado());
    }

    @Test
    void creaMatch63PorcientoCoincidenciaParcial() {
        PetDto lostPet = new PetDto();
        lostPet.setId(1L);
        lostPet.setRace("Labrador");
        lostPet.setColor("Negro");
        lostPet.setSize("Pequeño");

        PetDto foundPet = new PetDto();
        foundPet.setId(2L);
        foundPet.setRace("Labrador");
        foundPet.setColor("Marron");
        foundPet.setSize("Grande");

        when(petServiceClient.getPetById(1L)).thenReturn(lostPet);
        when(petServiceClient.getPetById(2L)).thenReturn(foundPet);
        when(matchRepository.save(any(Match.class))).thenAnswer(i -> {
            Match m = i.getArgument(0);
            m.setId(1L);
            return m;
        });
        when(matchCriteriaRepository.saveAll(anyList())).thenReturn(List.of());

        Match result = matchingService.createMatch(1L, 2L);
        assertEquals(63, result.getPorcentajeCoincidencia());
    }

    @Test
    void creaMatchLanzaExcepcionSiMascotaNoExiste() {
        when(petServiceClient.getPetById(1L)).thenReturn(null);
        assertThrows(RuntimeException.class, () -> matchingService.createMatch(1L, 2L));
    }

    @Test
    void matchingAutomaticoCreaMatchesSimilares() {
        PetDto lostPet = new PetDto();
        lostPet.setId(1L);
        lostPet.setRace("Labrador");
        lostPet.setColor("Marron");
        lostPet.setSize("Grande");

        PetDto foundPet = new PetDto();
        foundPet.setId(2L);
        foundPet.setRace("Labrador");
        foundPet.setColor("Marron");
        foundPet.setSize("Grande");

        when(petServiceClient.getPetsByStatus("PERDIDO")).thenReturn(List.of(lostPet));
        when(petServiceClient.getPetsByStatus("ENCONTRADO")).thenReturn(List.of(foundPet));
        when(petServiceClient.getPetById(1L)).thenReturn(lostPet);
        when(petServiceClient.getPetById(2L)).thenReturn(foundPet);
        when(matchRepository.save(any(Match.class))).thenReturn(new Match());
        when(matchCriteriaRepository.saveAll(anyList())).thenReturn(List.of());

        matchingService.runAutomaticMatching();
        verify(matchRepository, atLeastOnce()).save(any(Match.class));
    }

    @Test
    void matchingAutomaticoNoCreaMatchBajo60() {
        PetDto lostPet = new PetDto();
        lostPet.setId(1L);
        lostPet.setRace("Labrador");
        lostPet.setColor("Negro");
        lostPet.setSize("Pequeño");

        PetDto foundPet = new PetDto();
        foundPet.setId(2L);
        foundPet.setRace("Pastor");
        foundPet.setColor("Blanco");
        foundPet.setSize("Grande");

        when(petServiceClient.getPetsByStatus("PERDIDO")).thenReturn(List.of(lostPet));
        when(petServiceClient.getPetsByStatus("ENCONTRADO")).thenReturn(List.of(foundPet));

        matchingService.runAutomaticMatching();
        verify(matchRepository, never()).save(any(Match.class));
    }
}
