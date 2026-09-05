package com.coldchainsentinel.model;

import jakarta.persistence.*;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "inventory_items")
public class InventoryItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(optional = false)
    @JoinColumn(name = "storage_unit_id", nullable = false)
    private StorageUnit storageUnit;

    @Column(nullable = false)
    private int quantity;

    @Column(name = "batch_number", nullable = false)
    private String batchNumber;

    @Column(name = "expiry_date", nullable = false)
    private LocalDate expiryDate;

    @Column(name = "received_at", nullable = false)
    private Instant receivedAt = Instant.now();

    public InventoryItem() { }

    public InventoryItem(Product product, StorageUnit storageUnit, int quantity,
                          String batchNumber, LocalDate expiryDate) {
        this.product = product;
        this.storageUnit = storageUnit;
        this.quantity = quantity;
        this.batchNumber = batchNumber;
        this.expiryDate = expiryDate;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public StorageUnit getStorageUnit() { return storageUnit; }
    public void setStorageUnit(StorageUnit storageUnit) { this.storageUnit = storageUnit; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public String getBatchNumber() { return batchNumber; }
    public void setBatchNumber(String batchNumber) { this.batchNumber = batchNumber; }

    public LocalDate getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }

    public Instant getReceivedAt() { return receivedAt; }
    public void setReceivedAt(Instant receivedAt) { this.receivedAt = receivedAt; }

    public boolean isExpiringWithinDays(int days) {
        return !expiryDate.isAfter(LocalDate.now().plusDays(days));
    }
}
