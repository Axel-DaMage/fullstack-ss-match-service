package com.sanosysalvos.matchservice.client;

import com.sanosysalvos.matchservice.model.PetDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import java.util.List;

@Component
public class PetServiceClient {

    private final RestTemplate restTemplate;
    private final String petServiceUrl;

    public PetServiceClient(RestTemplate restTemplate,
                           @Value("${pet.service.url:http://pet-service:3001}") String petServiceUrl) {
        this.restTemplate = restTemplate;
        this.petServiceUrl = petServiceUrl;
    }

    public List<PetDto> getAllPets() {
        return restTemplate.exchange(
            petServiceUrl + "/api/pets",
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<List<PetDto>>() {}
        ).getBody();
    }

    public List<PetDto> getPetsByStatus(String status) {
        return restTemplate.exchange(
            petServiceUrl + "/api/pets/search/status/" + status,
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<List<PetDto>>() {}
        ).getBody();
    }

    public PetDto getPetById(Long id) {
        return restTemplate.getForObject(petServiceUrl + "/api/pets/" + id, PetDto.class);
    }
}