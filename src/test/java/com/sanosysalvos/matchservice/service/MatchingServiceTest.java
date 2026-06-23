package com.sanosysalvos.matchservice.service;

import com.sanosysalvos.matchservice.client.LocationServiceClient;
import com.sanosysalvos.matchservice.client.PetServiceClient;
import com.sanosysalvos.matchservice.model.Match;
import com.sanosysalvos.matchservice.model.MatchCriteria;
import com.sanosysalvos.matchservice.model.PetDto;
import com.sanosysalvos.matchservice.model.LocationDto;
import com.sanosysalvos.matchservice.repository.MatchCriteriaRepository;
import com.sanosysalvos.matchservice.repository.MatchRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
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
    private MatchRepository matchRepository;
    @Mock
    private MatchCriteriaRepository matchCriteriaRepository;
    @Mock
    private PetServiceClient petServiceClient;
    @Mock
    private LocationServiceClient locationServiceClient;

    @Captor
    private ArgumentCaptor<Match> matchCaptor;

    private MatchingService matchingService;

    @BeforeEach
    void setUp() {
        matchingService = new MatchingService(matchRepository, matchCriteriaRepository, petServiceClient, locationServiceClient);
    }

    @Test
    void getAllMatches_ShouldReturnAll() {
        when(matchRepository.findAll()).thenReturn(List.of(new Match(), new Match()));
        assertEquals(2, matchingService.getAllMatches().size());
    }

    @Test
    void getMatchById_ShouldReturnMatch() {
        Match match = new Match();
        match.setId(1L);
        when(matchRepository.findById(1L)).thenReturn(Optional.of(match));
        assertTrue(matchingService.getMatchById(1L).isPresent());
    }

    @Test
    void getMatchById_ShouldReturnEmptyWhenNotFound() {
        when(matchRepository.findById(99L)).thenReturn(Optional.empty());
        assertTrue(matchingService.getMatchById(99L).isEmpty());
    }

    @Test
    void createMatch_ShouldCalculateAndSave() {
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

        Match savedMatch = new Match();
        savedMatch.setId(1L);
        savedMatch.setMascotaPerdidaId(1L);
        savedMatch.setMascotaEncontradaId(2L);
        savedMatch.setPorcentajeCoincidencia(100);
        savedMatch.setEstado("PENDIENTE");

        when(petServiceClient.getPetById(1L)).thenReturn(lostPet);
        when(petServiceClient.getPetById(2L)).thenReturn(foundPet);
        when(matchRepository.save(any(Match.class))).thenReturn(savedMatch);
        when(matchCriteriaRepository.saveAll(anyList())).thenReturn(List.of());

        Match result = matchingService.createMatch(1L, 2L);
        assertEquals(1L, result.getId());
        assertEquals(100, result.getPorcentajeCoincidencia());
        verify(matchCriteriaRepository).saveAll(anyList());
    }

    @Test
    void createMatch_ShouldThrowWhenPetNotFound() {
        when(petServiceClient.getPetById(1L)).thenReturn(null);
        assertThrows(RuntimeException.class, () -> matchingService.createMatch(1L, 2L));
    }

    @Test
    void createMatch_ShouldCalculatePartialScore() {
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

        Match savedMatch = new Match();
        savedMatch.setId(1L);
        savedMatch.setPorcentajeCoincidencia(60);

        when(petServiceClient.getPetById(1L)).thenReturn(lostPet);
        when(petServiceClient.getPetById(2L)).thenReturn(foundPet);
        when(matchRepository.save(any(Match.class))).thenReturn(savedMatch);
        when(matchCriteriaRepository.saveAll(anyList())).thenReturn(List.of());

        Match result = matchingService.createMatch(1L, 2L);
        assertEquals(60, result.getPorcentajeCoincidencia());
    }

    @Test
    void updateMatchStatus_ShouldChangeAndSave() {
        Match match = new Match();
        match.setId(1L);
        match.setEstado("PENDIENTE");

        when(matchRepository.findById(1L)).thenReturn(Optional.of(match));
        when(matchRepository.save(any(Match.class))).thenAnswer(i -> i.getArgument(0));

        Match result = matchingService.updateMatchStatus(1L, "CONFIRMED");
        assertEquals("CONFIRMED", result.getEstado());
    }

    @Test
    void updateMatchStatus_ShouldThrowWhenNotFound() {
        when(matchRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> matchingService.updateMatchStatus(99L, "CONFIRMED"));
    }

    @Test
    void deleteMatch_ShouldDelete() {
        Match match = new Match();
        match.setId(1L);
        when(matchRepository.findById(1L)).thenReturn(Optional.of(match));

        matchingService.deleteMatch(1L);
        verify(matchRepository).delete(match);
    }

    @Test
    void deleteMatch_ShouldThrowWhenNotFound() {
        when(matchRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(RuntimeException.class, () -> matchingService.deleteMatch(99L));
    }

    @Test
    void getMatchesByStatus_ShouldReturnFiltered() {
        when(matchRepository.findByEstado("PENDIENTE")).thenReturn(List.of(new Match()));
        assertEquals(1, matchingService.getMatchesByStatus("PENDIENTE").size());
    }

    @Test
    void getMatchesByPercentage_ShouldReturnFiltered() {
        when(matchRepository.findByPorcentajeCoincidenciaGreaterThanEqual(80)).thenReturn(List.of(new Match()));
        assertEquals(1, matchingService.getMatchesByPercentage(80).size());
    }

    @Test
    void countMatchesByStatus_ShouldReturnCount() {
when(matchRepository.countByEstado("PENDIENTE")).thenReturn(5L);
assertEquals(5L, matchingService.countMatchesByStatus("PENDIENTE"));
    }

    @Test
    void getLocationByPetId_ShouldReturnLocation() {
        LocationDto loc = new LocationDto();
        loc.setPetId(1L);
        when(locationServiceClient.getLocationsByPetId(1L)).thenReturn(List.of(loc));

        LocationDto result = matchingService.getLocationByPetId(1L);
        assertEquals(1L, result.getPetId());
    }

    @Test
    void getLocationByPetId_ShouldReturnNullWhenNotFound() {
        when(locationServiceClient.getLocationsByPetId(99L)).thenReturn(List.of());
        assertNull(matchingService.getLocationByPetId(99L));
    }

    @Test
    void getAllLocations_ShouldReturnAll() {
        when(locationServiceClient.getAllLocations()).thenReturn(List.of(new LocationDto(), new LocationDto()));
        assertEquals(2, matchingService.getAllLocations().size());
    }

    @Test
    void runAutomaticMatching_ShouldCreateMatchesForSimilarPets() {
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

        Match savedMatch = new Match();
        savedMatch.setId(1L);
        savedMatch.setPorcentajeCoincidencia(100);

        when(petServiceClient.getPetsByStatus("PERDIDO")).thenReturn(List.of(lostPet));
        when(petServiceClient.getPetsByStatus("ENCONTRADO")).thenReturn(List.of(foundPet));
        when(petServiceClient.getPetById(1L)).thenReturn(lostPet);
        when(petServiceClient.getPetById(2L)).thenReturn(foundPet);
        when(matchRepository.save(any(Match.class))).thenReturn(savedMatch);
        when(matchCriteriaRepository.saveAll(anyList())).thenReturn(List.of());

        matchingService.runAutomaticMatching();
        verify(matchRepository, atLeastOnce()).save(any(Match.class));
    }

    @Test
    void runAutomaticMatching_ShouldNotCreateMatchBelow60() {
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
