package com.coldchainsentinel.service;

import com.coldchainsentinel.dto.InventoryRequest;
import com.coldchainsentinel.exception.ValidationException;
import com.coldchainsentinel.model.InventoryItem;
import com.coldchainsentinel.model.Product;
import com.coldchainsentinel.model.StorageUnit;
import com.coldchainsentinel.repository.InventoryItemRepository;
import com.coldchainsentinel.repository.ProductRepository;
import com.coldchainsentinel.repository.StorageUnitRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

    @Mock
    private InventoryItemRepository inventoryRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private StorageUnitRepository storageUnitRepository;

    @InjectMocks
    private InventoryService inventoryService;

    private Product product;
    private StorageUnit unit;

    @BeforeEach
    void setUp() {
        product = new Product("SKU-1", "TestDrug", 2.0, 8.0, 365);
        product.setId(1L);
        unit = new StorageUnit("Freezer A", "Site 1", "FREEZER");
        unit.setId(2L);
    }

    @Test
    void addItemSucceedsWithValidProductAndUnit() {
        InventoryRequest request = new InventoryRequest();
        request.setProductSku("SKU-1");
        request.setStorageUnitId(2L);
        request.setQuantity(50);
        request.setBatchNumber("BATCH-001");
        request.setExpiryDate(LocalDate.now().plusYears(1));

        when(productRepository.findBySku("SKU-1")).thenReturn(Optional.of(product));
        when(storageUnitRepository.findById(2L)).thenReturn(Optional.of(unit));
        when(inventoryRepository.save(any(InventoryItem.class))).thenAnswer(inv -> inv.getArgument(0));

        InventoryItem item = inventoryService.addItem(request);

        assertEquals(50, item.getQuantity());
        assertEquals("BATCH-001", item.getBatchNumber());
    }

    @Test
    void addItemThrowsForUnknownSku() {
        InventoryRequest request = new InventoryRequest();
        request.setProductSku("UNKNOWN-SKU");
        request.setStorageUnitId(2L);
        request.setQuantity(10);
        request.setBatchNumber("BATCH-002");
        request.setExpiryDate(LocalDate.now().plusMonths(6));

        when(productRepository.findBySku("UNKNOWN-SKU")).thenReturn(Optional.empty());

        assertThrows(ValidationException.class, () -> inventoryService.addItem(request));
    }

    @Test
    void listNearExpiryFiltersCorrectly() {
        InventoryItem soonToExpire = new InventoryItem(product, unit, 10, "B1", LocalDate.now().plusDays(5));
        InventoryItem farFromExpiry = new InventoryItem(product, unit, 20, "B2", LocalDate.now().plusYears(2));

        when(inventoryRepository.findAll()).thenReturn(List.of(soonToExpire, farFromExpiry));

        List<InventoryItem> result = inventoryService.listNearExpiry(30);

        assertEquals(1, result.size());
        assertEquals("B1", result.get(0).getBatchNumber());
    }
}
