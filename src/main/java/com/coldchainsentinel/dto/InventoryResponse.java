package com.coldchainsentinel.dto;

import java.time.Instant;
import java.time.LocalDate;

public class InventoryResponse {

    private Long id;
    private String productName;
    private String storageUnitName;
    private int quantity;
    private String batchNumber;
    private LocalDate expiryDate;
    private Instant receivedAt;
    private boolean nearExpiry;

    public InventoryResponse() { }

    public InventoryResponse(Long id, String productName, String storageUnitName, int quantity,
                              String batchNumber, LocalDate expiryDate, Instant receivedAt, boolean nearExpiry) {
        this.id = id;
        this.productName = productName;
        this.storageUnitName = storageUnitName;
        this.quantity = quantity;
        this.batchNumber = batchNumber;
        this.expiryDate = expiryDate;
        this.receivedAt = receivedAt;
        this.nearExpiry = nearExpiry;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }

    public String getStorageUnitName() { return storageUnitName; }
    public void setStorageUnitName(String storageUnitName) { this.storageUnitName = storageUnitName; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public String getBatchNumber() { return batchNumber; }
    public void setBatchNumber(String batchNumber) { this.batchNumber = batchNumber; }

    public LocalDate getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }

    public Instant getReceivedAt() { return receivedAt; }
    public void setReceivedAt(Instant receivedAt) { this.receivedAt = receivedAt; }

    public boolean isNearExpiry() { return nearExpiry; }
    public void setNearExpiry(boolean nearExpiry) { this.nearExpiry = nearExpiry; }
}
