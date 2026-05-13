package com.sanosysalvos.matchservice.service;

import com.sanosysalvos.matchservice.client.PetServiceClient;
import com.sanosysalvos.matchservice.client.LocationServiceClient;
import com.sanosysalvos.matchservice.model.Match;
import com.sanosysalvos.matchservice.model.MatchCriteria;
import com.sanosysalvos.matchservice.model.PetDto;
import com.sanosysalvos.matchservice.model.LocationDto;
import com.sanosysalvos.matchservice.repository.MatchCriteriaRepository;
import com.sanosysalvos.matchservice.repository.MatchRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class MatchingService {

    private final MatchRepository matchRepository;
    private final MatchCriteriaRepository matchCriteriaRepository;
    private final PetServiceClient petServiceClient;
    private final LocationServiceClient locationServiceClient;

    public MatchingService(MatchRepository matchRepository,
                          MatchCriteriaRepository matchCriteriaRepository,
                          PetServiceClient petServiceClient,
                          LocationServiceClient locationServiceClient) {
        this.matchRepository = matchRepository;
        this.matchCriteriaRepository = matchCriteriaRepository;
        this.petServiceClient = petServiceClient;
        this.locationServiceClient = locationServiceClient;
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
        match.setPetLostId(petLostId);
        match.setPetFoundId(petFoundId);
        match.setStatus("PENDING");

        List<MatchCriteria> criteriaList = calculateMatch(petLost, petFound);
        int totalScore = criteriaList.stream().mapToInt(MatchCriteria::getScore).sum();
        match.setMatchPercentage(totalScore / criteriaList.size());

        Match savedMatch = matchRepository.save(match);

        for (MatchCriteria criteria : criteriaList) {
            criteria.setMatch(savedMatch);
        }
        matchCriteriaRepository.saveAll(criteriaList);

        return savedMatch;
    }

    private List<MatchCriteria> calculateMatch(PetDto petLost, PetDto petFound) {
        List<MatchCriteria> criteriaList = new ArrayList<>();

        MatchCriteria raceMatch = new MatchCriteria();
        raceMatch.setCriteriaName("RACE");
        if (petLost.getRace() != null && petLost.getRace().equalsIgnoreCase(petFound.getRace())) {
            raceMatch.setScore(100);
        } else {
            raceMatch.setScore(30);
        }
        criteriaList.add(raceMatch);

        MatchCriteria colorMatch = new MatchCriteria();
        colorMatch.setCriteriaName("COLOR");
        if (petLost.getColor() != null && petLost.getColor().equalsIgnoreCase(petFound.getColor())) {
            colorMatch.setScore(100);
        } else {
            colorMatch.setScore(40);
        }
        criteriaList.add(colorMatch);

        MatchCriteria sizeMatch = new MatchCriteria();
        sizeMatch.setCriteriaName("SIZE");
        if (petLost.getSize() != null && petLost.getSize().equalsIgnoreCase(petFound.getSize())) {
            sizeMatch.setScore(100);
        } else {
            sizeMatch.setScore(50);
        }
        criteriaList.add(sizeMatch);

        return criteriaList;
    }

    @Transactional
    public Match updateMatchStatus(Long id, String status) {
        Match match = matchRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Match not found with id: " + id));
        match.setStatus(status);
        return matchRepository.save(match);
    }

    @Transactional
    public void deleteMatch(Long id) {
        Match match = matchRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Match not found with id: " + id));
        matchRepository.delete(match);
    }

    public List<Match> getMatchesByStatus(String status) {
        return matchRepository.findByStatus(status);
    }

    public List<Match> getMatchesByPercentage(Integer percentage) {
        return matchRepository.findByMatchPercentageGreaterThanEqual(percentage);
    }

    public long countMatchesByStatus(String status) {
        return matchRepository.countByStatus(status);
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
                int percentage = calculateSimpleMatch(lost, found);
                if (percentage >= 60) {
                    createMatch(lost.getId(), found.getId());
                }
            }
        }
    }

    private int calculateSimpleMatch(PetDto pet1, PetDto pet2) {
        int score = 0;
        int factors = 0;

        if (pet1.getRace() != null && pet1.getRace().equalsIgnoreCase(pet2.getRace())) {
            score += 33;
        }
        factors++;

        if (pet1.getColor() != null && pet1.getColor().equalsIgnoreCase(pet2.getColor())) {
            score += 33;
        }
        factors++;

        if (pet1.getSize() != null && pet1.getSize().equalsIgnoreCase(pet2.getSize())) {
            score += 34;
        }
        factors++;

        return factors > 0 ? score : 0;
    }
}