package com.sanosysalvos.matchservice.model;

import jakarta.persistence.*;

@Entity
@Table(name = "match_criteria")
public class MatchCriteria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_id", nullable = false)
    private Match match;

    @Column(name = "criteria_name", length = 50, nullable = false)
    private String criteriaName;

    @Column
    private Integer score;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Match getMatch() { return match; }
    public void setMatch(Match match) { this.match = match; }

    public String getCriteriaName() { return criteriaName; }
    public void setCriteriaName(String criteriaName) { this.criteriaName = criteriaName; }

    public Integer getScore() { return score; }
    public void setScore(Integer score) { this.score = score; }
}