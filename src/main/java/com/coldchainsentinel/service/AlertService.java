package com.coldchainsentinel.service;

import com.coldchainsentinel.exception.ResourceNotFoundException;
import com.coldchainsentinel.model.Alert;
import com.coldchainsentinel.model.Shipment;
import com.coldchainsentinel.repository.AlertRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AlertService {

    private final AlertRepository alertRepository;

    public AlertService(AlertRepository alertRepository) {
        this.alertRepository = alertRepository;
    }

    public Alert raise(Shipment shipment, Alert.Severity severity, String message) {
        Alert alert = new Alert(shipment, severity, message);
        return alertRepository.save(alert);
    }

    public List<Alert> listUnresolved() {
        return alertRepository.findByResolvedFalse();
    }

    public List<Alert> listForShipment(Long shipmentId) {
        return alertRepository.findByShipmentId(shipmentId);
    }

    public Alert resolve(Long alertId) {
        Alert alert = alertRepository.findById(alertId)
                .orElseThrow(() -> new ResourceNotFoundException("Alert not found: " + alertId));
        alert.setResolved(true);
        return alertRepository.save(alert);
    }
}
