package com.coldchainsentinel.dto;

import com.coldchainsentinel.model.SubscriptionStatus;

public class CheckoutResponse {

    private String paymentLinkId;
    private String paymentUrl;
    private String planName;
    private SubscriptionStatus status;

    public CheckoutResponse() { }

    public CheckoutResponse(String paymentLinkId, String paymentUrl, String planName, SubscriptionStatus status) {
        this.paymentLinkId = paymentLinkId;
        this.paymentUrl = paymentUrl;
        this.planName = planName;
        this.status = status;
    }

    public String getPaymentLinkId() { return paymentLinkId; }
    public void setPaymentLinkId(String paymentLinkId) { this.paymentLinkId = paymentLinkId; }

    public String getPaymentUrl() { return paymentUrl; }
    public void setPaymentUrl(String paymentUrl) { this.paymentUrl = paymentUrl; }

    public String getPlanName() { return planName; }
    public void setPlanName(String planName) { this.planName = planName; }

    public SubscriptionStatus getStatus() { return status; }
    public void setStatus(SubscriptionStatus status) { this.status = status; }
}
