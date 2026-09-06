package com.coldchainsentinel.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public class SubscriptionRequest {

    @NotBlank
    private String planName;

    @Positive
    private double amountInRupees;

    public SubscriptionRequest() { }

    public String getPlanName() { return planName; }
    public void setPlanName(String planName) { this.planName = planName; }

    public double getAmountInRupees() { return amountInRupees; }
    public void setAmountInRupees(double amountInRupees) { this.amountInRupees = amountInRupees; }
}
