package com.sanosysalvos.matchservice.strategy;

import com.sanosysalvos.matchservice.model.MatchCriteria;
import com.sanosysalvos.matchservice.model.PetDto;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class WeightedMatchingStrategy implements MatchingStrategy {

    @Override
    public List<MatchCriteria> calculate(PetDto petLost, PetDto petFound) {
        List<MatchCriteria> criteriaList = new ArrayList<>();

        MatchCriteria raceMatch = new MatchCriteria();
        raceMatch.setNombreCriterio("RAZA");
        if (petLost.getRace() != null && petLost.getRace().equalsIgnoreCase(petFound.getRace())) {
            raceMatch.setPuntaje(100);
        } else {
            raceMatch.setPuntaje(30);
        }
        criteriaList.add(raceMatch);

        MatchCriteria colorMatch = new MatchCriteria();
        colorMatch.setNombreCriterio("COLOR");
        if (petLost.getColor() != null && petLost.getColor().equalsIgnoreCase(petFound.getColor())) {
            colorMatch.setPuntaje(100);
        } else {
            colorMatch.setPuntaje(40);
        }
        criteriaList.add(colorMatch);

        MatchCriteria sizeMatch = new MatchCriteria();
        sizeMatch.setNombreCriterio("TAMAÑO");
        if (petLost.getSize() != null && petLost.getSize().equalsIgnoreCase(petFound.getSize())) {
            sizeMatch.setPuntaje(100);
        } else {
            sizeMatch.setPuntaje(50);
        }
        criteriaList.add(sizeMatch);

        return criteriaList;
    }

    @Override
    public int calculateSimple(PetDto pet1, PetDto pet2) {
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
