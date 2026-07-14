package com.sanosysalvos.matchservice.client;

import com.sanosysalvos.matchservice.model.LocationDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import java.util.List;

@Component
public class LocationServiceClient {

    private final RestTemplate restTemplate;
    private final String locationServiceUrl;

    public LocationServiceClient(RestTemplate restTemplate,
                                  @Value("${location.service.url:http://geo-service:3002}") String locationServiceUrl) {
        this.restTemplate = restTemplate;
        this.locationServiceUrl = locationServiceUrl;
    }

    public List<LocationDto> getAllLocations() {
        return restTemplate.exchange(
            locationServiceUrl + "/api/locations",
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<List<LocationDto>>() {}
        ).getBody();
    }

    public LocationDto getLocationById(Long id) {
        return restTemplate.getForObject(locationServiceUrl + "/api/locations/" + id, LocationDto.class);
    }

    public List<LocationDto> getLocationsByPetId(Long petId) {
        List<LocationDto> allLocations = getAllLocations();
        return allLocations.stream()
            .filter(loc -> loc.getPetId() != null && loc.getPetId().equals(petId))
            .toList();
    }

    public List<LocationDto> getLocationsByZone(String zone) {
        return restTemplate.exchange(
            locationServiceUrl + "/api/locations/search/zone/" + zone,
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<List<LocationDto>>() {}
        ).getBody();
    }
}