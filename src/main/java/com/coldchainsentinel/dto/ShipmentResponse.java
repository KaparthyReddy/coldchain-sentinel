package com.coldchainsentinel.dto;

import com.coldchainsentinel.model.ShipmentStatus;

import java.time.Instant;

public class ShipmentResponse {

    private Long id;
    private String productName;
    private String originUnitName;
    private String destinationUnitName;
    private ShipmentStatus status;
    private Instant departedAt;
    private Instant arrivedAt;
    private Instant createdAt;

    public ShipmentResponse() { }

    public ShipmentResponse(Long id, String productName, String originUnitName, String destinationUnitName,
                             ShipmentStatus status, Instant departedAt, Instant arrivedAt, Instant createdAt) {
        this.id = id;
        this.productName = productName;
        this.originUnitName = originUnitName;
        this.destinationUnitName = destinationUnitName;
        this.status = status;
        this.departedAt = departedAt;
        this.arrivedAt = arrivedAt;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getOriginUnitName() { return originUnitName; }
    public void setOriginUnitName(String originUnitName) { this.originUnitName = originUnitName; }

    public String getDestinationUnitName() { return destinationUnitName; }
    public void setDestinationUnitName(String destinationUnitName) { this.destinationUnitName = destinationUnitName; }

    public ShipmentStatus getStatus() { return status; }
    public void setStatus(ShipmentStatus status) { this.status = status; }

    public Instant getDepartedAt() { return departedAt; }
    public void setDepartedAt(Instant departedAt) { this.departedAt = departedAt; }

    public Instant getArrivedAt() { return arrivedAt; }
    public void setArrivedAt(Instant arrivedAt) { this.arrivedAt = arrivedAt; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
