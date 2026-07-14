package com.sanosysalvos.matchservice.service;

import com.sanosysalvos.matchservice.client.PetServiceClient;
import com.sanosysalvos.matchservice.client.LocationServiceClient;
import com.sanosysalvos.matchservice.model.Match;
import com.sanosysalvos.matchservice.model.MatchCriteria;
import com.sanosysalvos.matchservice.model.PetDto;
import com.sanosysalvos.matchservice.model.LocationDto;
import com.sanosysalvos.matchservice.repository.MatchCriteriaRepository;
import com.sanosysalvos.matchservice.repository.MatchRepository;
import com.sanosysalvos.matchservice.strategy.MatchingStrategy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class MatchingService {

    private final MatchRepository matchRepository;
    private final MatchCriteriaRepository matchCriteriaRepository;
    private final PetServiceClient petServiceClient;
    private final LocationServiceClient locationServiceClient;
    private final MatchingStrategy matchingStrategy;

    public MatchingService(MatchRepository matchRepository,
                          MatchCriteriaRepository matchCriteriaRepository,
                          PetServiceClient petServiceClient,
                          LocationServiceClient locationServiceClient,
                          MatchingStrategy matchingStrategy) {
        this.matchRepository = matchRepository;
        this.matchCriteriaRepository = matchCriteriaRepository;
        this.petServiceClient = petServiceClient;
        this.locationServiceClient = locationServiceClient;
        this.matchingStrategy = matchingStrategy;
    }

    public List<Match> getAllMatches() {
        return matchRepository.findAll();
    }

    public Optional<Match> getMatchById(Long id) {
        return matchRepository.findById(id);
    }

    @Transactional
    public Match createMatch(Long petLostId, Long petFoundId) {
        PetDto petLost = petServiceClient.getPetById(petLostId);
        PetDto petFound = petServiceClient.getPetById(petFoundId);

        if (petLost == null || petFound == null) {
            throw new RuntimeException("Pet not found");
        }

        Match match = new Match();
        match.setMascotaPerdidaId(petLostId);
        match.setMascotaEncontradaId(petFoundId);
        match.setEstado("PENDIENTE");

        List<MatchCriteria> criteriaList = matchingStrategy.calculate(petLost, petFound);
        int totalScore = criteriaList.stream().mapToInt(MatchCriteria::getPuntaje).sum();
        match.setPorcentajeCoincidencia(totalScore / criteriaList.size());

        Match savedMatch = matchRepository.save(match);

        for (MatchCriteria criteria : criteriaList) {
            criteria.setCoincidencia(savedMatch);
        }
        matchCriteriaRepository.saveAll(criteriaList);

        return savedMatch;
    }

    @Transactional
    public Match updateMatchStatus(Long id, String status) {
        Match match = matchRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Match not found with id: " + id));
        match.setEstado(status);
        return matchRepository.save(match);
    }

    @Transactional
    public void deleteMatch(Long id) {
        Match match = matchRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Match not found with id: " + id));
        matchRepository.delete(match);
    }

    public List<Match> getMatchesByStatus(String status) {
        return matchRepository.findByEstado(status);
    }

    public List<Match> getMatchesByPercentage(Integer percentage) {
        return matchRepository.findByPorcentajeCoincidenciaGreaterThanEqual(percentage);
    }

    public long countMatchesByStatus(String status) {
        return matchRepository.countByEstado(status);
    }

    public LocationDto getLocationByPetId(Long petId) {
        List<LocationDto> locations = locationServiceClient.getLocationsByPetId(petId);
        return locations.isEmpty() ? null : locations.get(0);
    }

    public List<LocationDto> getAllLocations() {
        return locationServiceClient.getAllLocations();
    }

    public void runAutomaticMatching() {
        List<PetDto> lostPets = petServiceClient.getPetsByStatus("LOST");
        List<PetDto> foundPets = petServiceClient.getPetsByStatus("FOUND");

        for (PetDto lost : lostPets) {
            for (PetDto found : foundPets) {
                int percentage = matchingStrategy.calculateSimple(lost, found);
                if (percentage >= 60) {
                    createMatch(lost.getId(), found.getId());
                }
            }
        }
    }

}