package com.sanosysalvos.matchservice.repository;

import com.sanosysalvos.matchservice.model.MatchCriteria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MatchCriteriaRepository extends JpaRepository<MatchCriteria, Long> {

    List<MatchCriteria> findByCoincidenciaId(Long matchId);
}