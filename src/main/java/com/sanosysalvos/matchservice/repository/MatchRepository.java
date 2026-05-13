package com.sanosysalvos.matchservice.repository;

import com.sanosysalvos.matchservice.model.Match;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MatchRepository extends JpaRepository<Match, Long> {

    List<Match> findByStatus(String status);

    List<Match> findByPetLostId(Long petLostId);

    List<Match> findByPetFoundId(Long petFoundId);

    List<Match> findByMatchPercentageGreaterThanEqual(Integer percentage);

    long countByStatus(String status);
}