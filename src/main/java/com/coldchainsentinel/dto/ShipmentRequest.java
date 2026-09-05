package com.coldchainsentinel.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ShipmentRequest {

    @NotBlank
    private String productSku;

    @NotNull
    private Long originUnitId;

    private Long destinationUnitId;

    public ShipmentRequest() { }

    public String getProductSku() { return productSku; }
    public void setProductSku(String productSku) { this.productSku = productSku; }

    public Long getOriginUnitId() { return originUnitId; }
    public void setOriginUnitId(Long originUnitId) { this.originUnitId = originUnitId; }

    public Long getDestinationUnitId() { return destinationUnitId; }
    public void setDestinationUnitId(Long destinationUnitId) { this.destinationUnitId = destinationUnitId; }
}
