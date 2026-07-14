package com.sanosysalvos.matchservice.model;

import jakarta.persistence.*;

@Entity
@Table(name = "match_criteria")
public class MatchCriteria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_coincidencia", nullable = false)
    private Match coincidencia;

    @Column(name = "nombre_criterio", length = 50, nullable = false)
    private String nombreCriterio;

    @Column(name = "puntaje")
    private Integer puntaje;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Match getCoincidencia() { return coincidencia; }
    public void setCoincidencia(Match coincidencia) { this.coincidencia = coincidencia; }

    public String getNombreCriterio() { return nombreCriterio; }
    public void setNombreCriterio(String nombreCriterio) { this.nombreCriterio = nombreCriterio; }

    public Integer getPuntaje() { return puntaje; }
    public void setPuntaje(Integer puntaje) { this.puntaje = puntaje; }
}