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

    @Column(name = "pet_lost_id", nullable = false)
    private Long petLostId;

    @Column(name = "pet_found_id", nullable = false)
    private Long petFoundId;

    @Column(name = "match_percentage")
    private Integer matchPercentage;

    @Column(length = 20, nullable = false)
    private String status;

    @OneToMany(mappedBy = "match", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<MatchCriteria> criteria;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getPetLostId() { return petLostId; }
    public void setPetLostId(Long petLostId) { this.petLostId = petLostId; }

    public Long getPetFoundId() { return petFoundId; }
    public void setPetFoundId(Long petFoundId) { this.petFoundId = petFoundId; }

    public Integer getMatchPercentage() { return matchPercentage; }
    public void setMatchPercentage(Integer matchPercentage) { this.matchPercentage = matchPercentage; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public List<MatchCriteria> getCriteria() { return criteria; }
    public void setCriteria(List<MatchCriteria> criteria) { this.criteria = criteria; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}