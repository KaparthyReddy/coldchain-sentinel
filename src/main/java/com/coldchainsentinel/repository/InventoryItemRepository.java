package com.coldchainsentinel.repository;

import com.coldchainsentinel.model.InventoryItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InventoryItemRepository extends JpaRepository<InventoryItem, Long> {
    List<InventoryItem> findByProductId(Long productId);
    List<InventoryItem> findByStorageUnitId(Long storageUnitId);
}
