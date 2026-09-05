package com.coldchainsentinel.controller;

import com.coldchainsentinel.dto.AlertResponse;
import com.coldchainsentinel.model.Alert;
import com.coldchainsentinel.service.AlertService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/alerts")
public class AlertController {

    private final AlertService alertService;

    public AlertController(AlertService alertService) {
        this.alertService = alertService;
    }

    @GetMapping
    public List<AlertResponse> listUnresolved() {
        return alertService.listUnresolved().stream().map(this::toResponse).toList();
    }

    @GetMapping("/shipment/{shipmentId}")
    public List<AlertResponse> listForShipment(@PathVariable Long shipmentId) {
        return alertService.listForShipment(shipmentId).stream().map(this::toResponse).toList();
    }

    @PostMapping("/{id}/resolve")
    @PreAuthorize("hasRole('PHARMACIST')")
    public AlertResponse resolve(@PathVariable Long id) {
        return toResponse(alertService.resolve(id));
    }

    private AlertResponse toResponse(Alert a) {
        return new AlertResponse(
                a.getId(),
                a.getShipment().getId(),
                a.getSeverity(),
                a.getMessage(),
                a.isResolved(),
                a.getCreatedAt()
        );
    }
}
