package com.sanosysalvos.matchservice.service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class PetServiceConsumer {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${pet-service.url}")
    private String petServiceUrl;

    @CircuitBreaker(name = "petServiceBreaker", fallbackMethod = "petServiceFallback")
    public List<Map<String, Object>> getPetReports() {
        return restTemplate.getForObject(petServiceUrl + "/", List.class);
    }

    public List<Map<String, Object>> petServiceFallback(Exception e) {
        // Fallback defensivo que devuelve una lista con un mapa de error
        return Collections.singletonList(Collections.singletonMap("error", "Pet Service depends is unavailable. Circuit open."));
    }
}
