package com.coldchainsentinel.service;

import com.coldchainsentinel.dto.ShipmentRequest;
import com.coldchainsentinel.exception.ResourceNotFoundException;
import com.coldchainsentinel.exception.ValidationException;
import com.coldchainsentinel.model.*;
import com.coldchainsentinel.repository.ProductRepository;
import com.coldchainsentinel.repository.ShipmentRepository;
import com.coldchainsentinel.repository.StorageUnitRepository;
import com.coldchainsentinel.repository.TemperatureReadingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class ShipmentService {

    private final ShipmentRepository shipmentRepository;
    private final ProductRepository productRepository;
    private final StorageUnitRepository storageUnitRepository;
    private final TemperatureReadingRepository readingRepository;
    private final TemperatureValidationService validationService;
    private final AlertService alertService;

    public ShipmentService(ShipmentRepository shipmentRepository, ProductRepository productRepository,
                            StorageUnitRepository storageUnitRepository,
                            TemperatureReadingRepository readingRepository,
                            TemperatureValidationService validationService, AlertService alertService) {
        this.shipmentRepository = shipmentRepository;
        this.productRepository = productRepository;
        this.storageUnitRepository = storageUnitRepository;
        this.readingRepository = readingRepository;
        this.validationService = validationService;
        this.alertService = alertService;
    }

    @Transactional
    public Shipment createShipment(ShipmentRequest request) {
        Product product = productRepository.findBySku(request.getProductSku())
                .orElseThrow(() -> new ValidationException("Unknown product SKU: " + request.getProductSku()));

        StorageUnit origin = storageUnitRepository.findById(request.getOriginUnitId())
                .orElseThrow(() -> new ValidationException("Unknown origin unit: " + request.getOriginUnitId()));

        StorageUnit destination = null;
        if (request.getDestinationUnitId() != null) {
            destination = storageUnitRepository.findById(request.getDestinationUnitId())
                    .orElseThrow(() -> new ValidationException("Unknown destination unit: " + request.getDestinationUnitId()));
        }

        return shipmentRepository.save(new Shipment(product, origin, destination));
    }

    public Shipment getById(Long id) {
        return shipmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shipment not found: " + id));
    }

    public List<Shipment> listAll() {
        return shipmentRepository.findAll();
    }

    @Transactional
    public Shipment markDeparted(Long shipmentId) {
        Shipment shipment = getById(shipmentId);
        if (shipment.getStatus() != ShipmentStatus.PENDING) {
            throw new ValidationException("Only PENDING shipments can depart (was " + shipment.getStatus() + ")");
        }
        shipment.setStatus(ShipmentStatus.IN_TRANSIT);
        shipment.setDepartedAt(Instant.now());
        return shipmentRepository.save(shipment);
    }

    @Transactional
    public Shipment markArrived(Long shipmentId) {
        Shipment shipment = getById(shipmentId);
        if (shipment.getStatus() != ShipmentStatus.IN_TRANSIT) {
            throw new ValidationException("Only IN_TRANSIT shipments can arrive (was " + shipment.getStatus() + ")");
        }
        shipment.setStatus(ShipmentStatus.DELIVERED);
        shipment.setArrivedAt(Instant.now());
        return shipmentRepository.save(shipment);
    }

    /**
     * Ingests a temperature reading for a shipment and, if it violates the
     * product's safe range, raises an alert and flags the shipment as
     * COMPROMISED.
     */
    @Transactional
    public TemperatureReading ingestReading(Long shipmentId, double temperatureC) {
        Shipment shipment = getById(shipmentId);
        TemperatureReading reading = readingRepository.save(new TemperatureReading(shipment, temperatureC));

        Optional<TemperatureValidationService.Violation> violation =
                validationService.validate(shipment.getProduct(), temperatureC);

        if (violation.isPresent()) {
            alertService.raise(shipment, violation.get().severity(), violation.get().message());
            shipment.setStatus(ShipmentStatus.COMPROMISED);
            shipmentRepository.save(shipment);
        }

        return reading;
    }

    public List<TemperatureReading> getReadings(Long shipmentId) {
        return readingRepository.findByShipmentIdOrderByRecordedAtAsc(shipmentId);
    }
}
