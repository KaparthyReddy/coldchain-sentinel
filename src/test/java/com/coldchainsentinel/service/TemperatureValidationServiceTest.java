package com.coldchainsentinel.service;

import com.coldchainsentinel.model.Alert;
import com.coldchainsentinel.model.Product;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class TemperatureValidationServiceTest {

    private final TemperatureValidationService service = new TemperatureValidationService();
    private final Product product = new Product("SKU-1", "TestVaccine", 2.0, 8.0, 365);

    @Test
    void withinRangeProducesNoViolation() {
        Optional<TemperatureValidationService.Violation> result = service.validate(product, 5.0);
        assertTrue(result.isEmpty());
    }

    @Test
    void smallDeviationBelowRangeIsMedium() {
        // min=2, max=8, rangeWidth=6; observed=1 -> deviation=1, ratio=0.167
        Optional<TemperatureValidationService.Violation> result = service.validate(product, 1.0);
        assertTrue(result.isPresent());
        assertEquals(Alert.Severity.MEDIUM, result.get().severity());
    }

    @Test
    void moderateDeviationIsHigh() {
        // observed=-1 -> deviation=3, ratio=0.5
        Optional<TemperatureValidationService.Violation> result = service.validate(product, -1.0);
        assertTrue(result.isPresent());
        assertEquals(Alert.Severity.HIGH, result.get().severity());
    }

    @Test
    void largeDeviationAboveRangeIsCritical() {
        // observed=20 -> deviation=12, ratio=2.0
        Optional<TemperatureValidationService.Violation> result = service.validate(product, 20.0);
        assertTrue(result.isPresent());
        assertEquals(Alert.Severity.CRITICAL, result.get().severity());
        assertTrue(result.get().message().contains("TestVaccine"));
    }
}
