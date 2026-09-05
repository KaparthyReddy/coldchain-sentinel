package com.coldchainsentinel.model;

import jakarta.persistence.*;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String sku;

    @Column(nullable = false)
    private String name;

    @Column(name = "min_safe_temp_c", nullable = false)
    private double minSafeTempC;

    @Column(name = "max_safe_temp_c", nullable = false)
    private double maxSafeTempC;

    @Column(name = "shelf_life_days", nullable = false)
    private int shelfLifeDays;

    public Product() { }

    public Product(String sku, String name, double minSafeTempC, double maxSafeTempC, int shelfLifeDays) {
        this.sku = sku;
        this.name = name;
        this.minSafeTempC = minSafeTempC;
        this.maxSafeTempC = maxSafeTempC;
        this.shelfLifeDays = shelfLifeDays;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getMinSafeTempC() { return minSafeTempC; }
    public void setMinSafeTempC(double minSafeTempC) { this.minSafeTempC = minSafeTempC; }

    public double getMaxSafeTempC() { return maxSafeTempC; }
    public void setMaxSafeTempC(double maxSafeTempC) { this.maxSafeTempC = maxSafeTempC; }

    public int getShelfLifeDays() { return shelfLifeDays; }
    public void setShelfLifeDays(int shelfLifeDays) { this.shelfLifeDays = shelfLifeDays; }

    public boolean isWithinSafeRange(double temperatureC) {
        return temperatureC >= minSafeTempC && temperatureC <= maxSafeTempC;
    }
}
