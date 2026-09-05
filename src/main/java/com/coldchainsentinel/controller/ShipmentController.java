package com.coldchainsentinel.controller;

import com.coldchainsentinel.dto.ReadingRequest;
import com.coldchainsentinel.dto.ShipmentRequest;
import com.coldchainsentinel.dto.ShipmentResponse;
import com.coldchainsentinel.model.Shipment;
import com.coldchainsentinel.model.TemperatureReading;
import com.coldchainsentinel.service.ShipmentService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/shipments")
public class ShipmentController {

    private final ShipmentService shipmentService;

    public ShipmentController(ShipmentService shipmentService) {
        this.shipmentService = shipmentService;
    }

    @PostMapping
    @PreAuthorize("hasRole('LOGISTICS')")
    public ShipmentResponse create(@Valid @RequestBody ShipmentRequest request) {
        return toResponse(shipmentService.createShipment(request));
    }

    @GetMapping
    public List<ShipmentResponse> listAll() {
        return shipmentService.listAll().stream().map(this::toResponse).toList();
    }

    @GetMapping("/{id}")
    public ShipmentResponse getById(@PathVariable Long id) {
        return toResponse(shipmentService.getById(id));
    }

    @PostMapping("/{id}/depart")
    @PreAuthorize("hasRole('LOGISTICS')")
    public ShipmentResponse markDeparted(@PathVariable Long id) {
        return toResponse(shipmentService.markDeparted(id));
    }

    @PostMapping("/{id}/arrive")
    @PreAuthorize("hasRole('LOGISTICS')")
    public ShipmentResponse markArrived(@PathVariable Long id) {
        return toResponse(shipmentService.markArrived(id));
    }

    @PostMapping("/{id}/readings")
    @PreAuthorize("hasRole('LOGISTICS')")
    public TemperatureReading ingestReading(@PathVariable Long id, @Valid @RequestBody ReadingRequest request) {
        return shipmentService.ingestReading(id, request.getTemperatureC());
    }

    @GetMapping("/{id}/readings")
    public List<TemperatureReading> getReadings(@PathVariable Long id) {
        return shipmentService.getReadings(id);
    }

    private ShipmentResponse toResponse(Shipment s) {
        return new ShipmentResponse(
                s.getId(),
                s.getProduct().getName(),
                s.getOriginUnit().getName(),
                s.getDestinationUnit() != null ? s.getDestinationUnit().getName() : null,
                s.getStatus(),
                s.getDepartedAt(),
                s.getArrivedAt(),
                s.getCreatedAt()
        );
    }
}
