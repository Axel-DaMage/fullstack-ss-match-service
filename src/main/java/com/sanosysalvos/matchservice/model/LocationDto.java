package com.sanosysalvos.matchservice.model;

import java.time.LocalDateTime;

public class LocationDto {

    private Long id;
    private Long petId;
    private Double latitude;
    private Double longitude;
    private String zone;
    private String address;
    private LocalDateTime reportedAt;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getPetId() { return petId; }
    public void setPetId(Long petId) { this.petId = petId; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public String getZone() { return zone; }
    public void setZone(String zone) { this.zone = zone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public LocalDateTime getReportedAt() { return reportedAt; }
    public void setReportedAt(LocalDateTime reportedAt) { this.reportedAt = reportedAt; }
}