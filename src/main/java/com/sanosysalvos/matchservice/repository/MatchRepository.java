package com.sanosysalvos.matchservice.repository;

import com.sanosysalvos.matchservice.model.Match;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MatchRepository extends JpaRepository<Match, Long> {

    List<Match> findByEstado(String estado);

    List<Match> findByMascotaPerdidaId(Long mascotaPerdidaId);

    List<Match> findByMascotaEncontradaId(Long mascotaEncontradaId);

    long countByEstado(String estado);

    List<Match> findByPorcentajeCoincidencia(Integer percentage);

    List<Match> findByPorcentajeCoincidenciaGreaterThanEqual(Integer percentage);
}