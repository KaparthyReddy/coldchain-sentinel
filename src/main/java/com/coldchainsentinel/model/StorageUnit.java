package com.coldchainsentinel.model;

import jakarta.persistence.*;

@Entity
@Table(name = "storage_units")
public class StorageUnit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String location;

    @Column(name = "unit_type", nullable = false)
    private String unitType; // e.g. "FREEZER", "REFRIGERATOR", "WAREHOUSE"

    public StorageUnit() { }

    public StorageUnit(String name, String location, String unitType) {
        this.name = name;
        this.location = location;
        this.unitType = unitType;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getUnitType() { return unitType; }
    public void setUnitType(String unitType) { this.unitType = unitType; }
}
