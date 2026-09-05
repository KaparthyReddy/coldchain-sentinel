package com.coldchainsentinel.service;

import com.coldchainsentinel.model.Alert;
import com.coldchainsentinel.model.Product;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Pure validation logic: given a product's safe range and an observed
 * temperature, decides whether an excursion occurred and how severe it is.
 * Kept separate from persistence so the rule itself is easy to unit-test.
 */
@Service
public class TemperatureValidationService {

    public Optional<Violation> validate(Product product, double observedTempC) {
        if (product.isWithinSafeRange(observedTempC)) {
            return Optional.empty();
        }

        double min = product.getMinSafeTempC();
        double max = product.getMaxSafeTempC();
        double deviation = observedTempC < min ? min - observedTempC : observedTempC - max;
        double rangeWidth = Math.max(max - min, 0.1); // avoid divide-by-zero on razor-thin ranges

        Alert.Severity severity;
        double deviationRatio = deviation / rangeWidth;
        if (deviationRatio >= 1.0) {
            severity = Alert.Severity.CRITICAL;
        } else if (deviationRatio >= 0.5) {
            severity = Alert.Severity.HIGH;
        } else {
            severity = Alert.Severity.MEDIUM;
        }

        String message = String.format(
                "Temperature excursion for %s: recorded %.2f\u00B0C, safe range is %.2f\u00B0C to %.2f\u00B0C",
                product.getName(), observedTempC, min, max);

        return Optional.of(new Violation(severity, message));
    }

    public record Violation(Alert.Severity severity, String message) { }
}
