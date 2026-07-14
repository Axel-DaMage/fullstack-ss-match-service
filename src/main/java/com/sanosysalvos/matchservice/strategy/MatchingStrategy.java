package com.sanosysalvos.matchservice.strategy;

import com.sanosysalvos.matchservice.model.MatchCriteria;
import com.sanosysalvos.matchservice.model.PetDto;

import java.util.List;

public interface MatchingStrategy {
    List<MatchCriteria> calculate(PetDto petLost, PetDto petFound);
    int calculateSimple(PetDto pet1, PetDto pet2);
}
