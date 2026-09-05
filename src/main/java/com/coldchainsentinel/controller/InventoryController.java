package com.coldchainsentinel.controller;

import com.coldchainsentinel.dto.InventoryRequest;
import com.coldchainsentinel.dto.InventoryResponse;
import com.coldchainsentinel.model.InventoryItem;
import com.coldchainsentinel.service.InventoryService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/inventory")
public class InventoryController {

    private static final int EXPIRY_WARNING_WINDOW_DAYS = 30;

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @PostMapping
    @PreAuthorize("hasRole('PHARMACIST')")
    public InventoryResponse addItem(@Valid @RequestBody InventoryRequest request) {
        return toResponse(inventoryService.addItem(request));
    }

    @GetMapping
    public List<InventoryResponse> listAll() {
        return inventoryService.listAll().stream().map(this::toResponse).toList();
    }

    @GetMapping("/{id}")
    public InventoryResponse getById(@PathVariable Long id) {
        return toResponse(inventoryService.getById(id));
    }

    @GetMapping("/product/{productId}")
    public List<InventoryResponse> listByProduct(@PathVariable Long productId) {
        return inventoryService.listByProduct(productId).stream().map(this::toResponse).toList();
    }

    @GetMapping("/near-expiry")
    public List<InventoryResponse> listNearExpiry(
            @RequestParam(required = false, defaultValue = "0") int withinDays) {
        int window = withinDays > 0 ? withinDays : EXPIRY_WARNING_WINDOW_DAYS;
        return inventoryService.listNearExpiry(window).stream().map(this::toResponse).toList();
    }

    private InventoryResponse toResponse(InventoryItem item) {
        return new InventoryResponse(
                item.getId(),
                item.getProduct().getName(),
                item.getStorageUnit().getName(),
                item.getQuantity(),
                item.getBatchNumber(),
                item.getExpiryDate(),
                item.getReceivedAt(),
                item.isExpiringWithinDays(EXPIRY_WARNING_WINDOW_DAYS)
        );
    }
}
