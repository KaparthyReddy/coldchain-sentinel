package com.coldchainsentinel.dto;

import jakarta.validation.constraints.NotNull;

public class ReadingRequest {

    @NotNull
    private Double temperatureC;

    public ReadingRequest() { }

    public ReadingRequest(Double temperatureC) {
        this.temperatureC = temperatureC;
    }

    public Double getTemperatureC() { return temperatureC; }
    public void setTemperatureC(Double temperatureC) { this.temperatureC = temperatureC; }
}
