package com.sanosysalvos.matchservice.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "matches")
public class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "mascota_perdida_id", nullable = false)
    private Long mascotaPerdidaId;

    @Column(name = "mascota_encontrada_id", nullable = false)
    private Long mascotaEncontradaId;

    @Column(name = "porcentaje_coincidencia")
    private Integer porcentajeCoincidencia;

    @Column(name = "estado", length = 20, nullable = false)
    private String estado;

    @OneToMany(mappedBy = "coincidencia", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<MatchCriteria> criterios;

    @Column(name = "creado_en", nullable = false, updatable = false)
    private LocalDateTime creadoEn;

    @Column(name = "actualizado_en")
    private LocalDateTime actualizadoEn;

    @PrePersist
    protected void onCreate() {
        creadoEn = LocalDateTime.now();
        actualizadoEn = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        actualizadoEn = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getMascotaPerdidaId() { return mascotaPerdidaId; }
    public void setMascotaPerdidaId(Long mascotaPerdidaId) { this.mascotaPerdidaId = mascotaPerdidaId; }

    public Long getMascotaEncontradaId() { return mascotaEncontradaId; }
    public void setMascotaEncontradaId(Long mascotaEncontradaId) { this.mascotaEncontradaId = mascotaEncontradaId; }

    public Integer getPorcentajeCoincidencia() { return porcentajeCoincidencia; }
    public void setPorcentajeCoincidencia(Integer porcentajeCoincidencia) { this.porcentajeCoincidencia = porcentajeCoincidencia; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public List<MatchCriteria> getCriterios() { return criterios; }
    public void setCriterios(List<MatchCriteria> criterios) { this.criterios = criterios; }

    public LocalDateTime getCreadoEn() { return creadoEn; }
    public void setCreadoEn(LocalDateTime creadoEn) { this.creadoEn = creadoEn; }

    public LocalDateTime getActualizadoEn() { return actualizadoEn; }
    public void setActualizadoEn(LocalDateTime actualizadoEn) { this.actualizadoEn = actualizadoEn; }
}