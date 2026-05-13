package com.sanosysalvos.matchservice.controller;

import com.sanosysalvos.matchservice.model.Match;
import com.sanosysalvos.matchservice.service.MatchingService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/matching")
public class MatchController {

    private final MatchingService matchingService;

    public MatchController(MatchingService matchingService) {
        this.matchingService = matchingService;
    }

    @GetMapping
    public ResponseEntity<List<Match>> getAllMatches() {
        return ResponseEntity.ok(matchingService.getAllMatches());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Match> getMatchById(@PathVariable Long id) {
        return matchingService.getMatchById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Match> createMatch(@RequestBody Map<String, Long> request) {
        Long petLostId = request.get("petLostId");
        Long petFoundId = request.get("petFoundId");
        if (petLostId == null || petFoundId == null) {
            return ResponseEntity.badRequest().build();
        }
        Match createdMatch = matchingService.createMatch(petLostId, petFoundId);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdMatch);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Match> updateMatchStatus(@PathVariable Long id, @RequestBody Map<String, String> request) {
        String status = request.get("status");
        if (status == null) {
            return ResponseEntity.badRequest().build();
        }
        try {
            Match updatedMatch = matchingService.updateMatchStatus(id, status);
            return ResponseEntity.ok(updatedMatch);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMatch(@PathVariable Long id) {
        try {
            matchingService.deleteMatch(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/search/status/{status}")
    public ResponseEntity<List<Match>> getMatchesByStatus(@PathVariable String status) {
        return ResponseEntity.ok(matchingService.getMatchesByStatus(status));
    }

    @GetMapping("/search/percentage/{percentage}")
    public ResponseEntity<List<Match>> getMatchesByPercentage(@PathVariable Integer percentage) {
        return ResponseEntity.ok(matchingService.getMatchesByPercentage(percentage));
    }

    @GetMapping("/totals/status")
    public ResponseEntity<Map<String, Long>> getTotalsByStatus() {
        long pendingCount = matchingService.countMatchesByStatus("PENDING");
        long confirmedCount = matchingService.countMatchesByStatus("CONFIRMED");
        long rejectedCount = matchingService.countMatchesByStatus("REJECTED");
        return ResponseEntity.ok(Map.of("pending", pendingCount, "confirmed", confirmedCount, "rejected", rejectedCount));
    }

    @PostMapping("/run-automatic")
    public ResponseEntity<String> runAutomaticMatching() {
        matchingService.runAutomaticMatching();
        return ResponseEntity.ok("Automatic matching completed");
    }
}