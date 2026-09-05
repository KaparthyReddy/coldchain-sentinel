package com.coldchainsentinel.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "temperature_readings")
public class TemperatureReading {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "shipment_id", nullable = false)
    private Shipment shipment;

    @Column(name = "temperature_c", nullable = false)
    private double temperatureC;

    @Column(name = "recorded_at", nullable = false)
    private Instant recordedAt = Instant.now();

    public TemperatureReading() { }

    public TemperatureReading(Shipment shipment, double temperatureC) {
        this.shipment = shipment;
        this.temperatureC = temperatureC;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Shipment getShipment() { return shipment; }
    public void setShipment(Shipment shipment) { this.shipment = shipment; }

    public double getTemperatureC() { return temperatureC; }
    public void setTemperatureC(double temperatureC) { this.temperatureC = temperatureC; }

    public Instant getRecordedAt() { return recordedAt; }
    public void setRecordedAt(Instant recordedAt) { this.recordedAt = recordedAt; }
}
