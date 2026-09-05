package com.coldchainsentinel.dto;

import com.coldchainsentinel.model.Alert;

import java.time.Instant;

public class AlertResponse {

    private Long id;
    private Long shipmentId;
    private Alert.Severity severity;
    private String message;
    private boolean resolved;
    private Instant createdAt;

    public AlertResponse() { }

    public AlertResponse(Long id, Long shipmentId, Alert.Severity severity, String message,
                          boolean resolved, Instant createdAt) {
        this.id = id;
        this.shipmentId = shipmentId;
        this.severity = severity;
        this.message = message;
        this.resolved = resolved;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getShipmentId() { return shipmentId; }
    public void setShipmentId(Long shipmentId) { this.shipmentId = shipmentId; }

    public Alert.Severity getSeverity() { return severity; }
    public void setSeverity(Alert.Severity severity) { this.severity = severity; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public boolean isResolved() { return resolved; }
    public void setResolved(boolean resolved) { this.resolved = resolved; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
