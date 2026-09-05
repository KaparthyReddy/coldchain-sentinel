package com.coldchainsentinel.model;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "shipments")
public class Shipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(optional = false)
    @JoinColumn(name = "origin_unit_id", nullable = false)
    private StorageUnit originUnit;

    @ManyToOne
    @JoinColumn(name = "destination_unit_id")
    private StorageUnit destinationUnit;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ShipmentStatus status = ShipmentStatus.PENDING;

    @Column(name = "departed_at")
    private Instant departedAt;

    @Column(name = "arrived_at")
    private Instant arrivedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    public Shipment() { }

    public Shipment(Product product, StorageUnit originUnit, StorageUnit destinationUnit) {
        this.product = product;
        this.originUnit = originUnit;
        this.destinationUnit = destinationUnit;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Product getProduct() { return product; }
    public void setProduct(Product product) { this.product = product; }

    public StorageUnit getOriginUnit() { return originUnit; }
    public void setOriginUnit(StorageUnit originUnit) { this.originUnit = originUnit; }

    public StorageUnit getDestinationUnit() { return destinationUnit; }
    public void setDestinationUnit(StorageUnit destinationUnit) { this.destinationUnit = destinationUnit; }

    public ShipmentStatus getStatus() { return status; }
    public void setStatus(ShipmentStatus status) { this.status = status; }

    public Instant getDepartedAt() { return departedAt; }
    public void setDepartedAt(Instant departedAt) { this.departedAt = departedAt; }

    public Instant getArrivedAt() { return arrivedAt; }
    public void setArrivedAt(Instant arrivedAt) { this.arrivedAt = arrivedAt; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
}
