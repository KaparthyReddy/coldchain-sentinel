package com.coldchainsentinel.service;

import com.coldchainsentinel.dto.InventoryRequest;
import com.coldchainsentinel.exception.ResourceNotFoundException;
import com.coldchainsentinel.exception.ValidationException;
import com.coldchainsentinel.model.InventoryItem;
import com.coldchainsentinel.model.Product;
import com.coldchainsentinel.model.StorageUnit;
import com.coldchainsentinel.repository.InventoryItemRepository;
import com.coldchainsentinel.repository.ProductRepository;
import com.coldchainsentinel.repository.StorageUnitRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InventoryService {

    private static final int DEFAULT_EXPIRY_WARNING_DAYS = 30;

    private final InventoryItemRepository inventoryRepository;
    private final ProductRepository productRepository;
    private final StorageUnitRepository storageUnitRepository;

    public InventoryService(InventoryItemRepository inventoryRepository, ProductRepository productRepository,
                             StorageUnitRepository storageUnitRepository) {
        this.inventoryRepository = inventoryRepository;
        this.productRepository = productRepository;
        this.storageUnitRepository = storageUnitRepository;
    }

    public InventoryItem addItem(InventoryRequest request) {
        Product product = productRepository.findBySku(request.getProductSku())
                .orElseThrow(() -> new ValidationException("Unknown product SKU: " + request.getProductSku()));

        StorageUnit unit = storageUnitRepository.findById(request.getStorageUnitId())
                .orElseThrow(() -> new ValidationException("Unknown storage unit: " + request.getStorageUnitId()));

        InventoryItem item = new InventoryItem(product, unit, request.getQuantity(),
                request.getBatchNumber(), request.getExpiryDate());
        return inventoryRepository.save(item);
    }

    public InventoryItem getById(Long id) {
        return inventoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory item not found: " + id));
    }

    public List<InventoryItem> listAll() {
        return inventoryRepository.findAll();
    }

    public List<InventoryItem> listByProduct(Long productId) {
        return inventoryRepository.findByProductId(productId);
    }

    public List<InventoryItem> listNearExpiry(int withinDays) {
        int window = withinDays > 0 ? withinDays : DEFAULT_EXPIRY_WARNING_DAYS;
        return inventoryRepository.findAll().stream()
                .filter(item -> item.isExpiringWithinDays(window))
                .toList();
    }
}
