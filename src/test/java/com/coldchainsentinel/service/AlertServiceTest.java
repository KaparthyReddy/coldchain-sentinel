package com.coldchainsentinel.service;

import com.coldchainsentinel.exception.ResourceNotFoundException;
import com.coldchainsentinel.model.Alert;
import com.coldchainsentinel.model.Product;
import com.coldchainsentinel.model.Shipment;
import com.coldchainsentinel.model.StorageUnit;
import com.coldchainsentinel.repository.AlertRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlertServiceTest {

    @Mock
    private AlertRepository alertRepository;

    @InjectMocks
    private AlertService alertService;

    private Shipment shipment;

    @BeforeEach
    void setUp() {
        Product product = new Product("SKU-1", "TestDrug", 2.0, 8.0, 365);
        StorageUnit unit = new StorageUnit("Freezer A", "Site 1", "FREEZER");
        shipment = new Shipment(product, unit, null);
        shipment.setId(1L);
    }

    @Test
    void raiseSavesAndReturnsAlert() {
        when(alertRepository.save(any(Alert.class))).thenAnswer(inv -> inv.getArgument(0));

        Alert alert = alertService.raise(shipment, Alert.Severity.HIGH, "Excursion detected");

        assertEquals(Alert.Severity.HIGH, alert.getSeverity());
        assertFalse(alert.isResolved());
        verify(alertRepository).save(any(Alert.class));
    }

    @Test
    void listUnresolvedDelegatesToRepository() {
        Alert alert = new Alert(shipment, Alert.Severity.LOW, "test");
        when(alertRepository.findByResolvedFalse()).thenReturn(List.of(alert));

        List<Alert> result = alertService.listUnresolved();

        assertEquals(1, result.size());
        verify(alertRepository).findByResolvedFalse();
    }

    @Test
    void resolveMarksAlertResolved() {
        Alert alert = new Alert(shipment, Alert.Severity.HIGH, "test");
        alert.setId(5L);
        when(alertRepository.findById(5L)).thenReturn(Optional.of(alert));
        when(alertRepository.save(any(Alert.class))).thenAnswer(inv -> inv.getArgument(0));

        Alert resolved = alertService.resolve(5L);

        assertTrue(resolved.isResolved());
    }

    @Test
    void resolveThrowsWhenAlertMissing() {
        when(alertRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> alertService.resolve(99L));
    }
}
