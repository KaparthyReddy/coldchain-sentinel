package com.coldchainsentinel.model;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "subscriptions")
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "plan_name", nullable = false)
    private String planName;

    @Column(name = "amount_in_paise", nullable = false)
    private long amountInPaise;

    @Column(name = "razorpay_payment_link_id", nullable = false, unique = true)
    private String razorpayPaymentLinkId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SubscriptionStatus status = SubscriptionStatus.PENDING;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @Column(name = "activated_at")
    private Instant activatedAt;

    public Subscription() { }

    public Subscription(User user, String planName, long amountInPaise, String razorpayPaymentLinkId) {
        this.user = user;
        this.planName = planName;
        this.amountInPaise = amountInPaise;
        this.razorpayPaymentLinkId = razorpayPaymentLinkId;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getPlanName() { return planName; }
    public void setPlanName(String planName) { this.planName = planName; }

    public long getAmountInPaise() { return amountInPaise; }
    public void setAmountInPaise(long amountInPaise) { this.amountInPaise = amountInPaise; }

    public String getRazorpayPaymentLinkId() { return razorpayPaymentLinkId; }
    public void setRazorpayPaymentLinkId(String razorpayPaymentLinkId) { this.razorpayPaymentLinkId = razorpayPaymentLinkId; }

    public SubscriptionStatus getStatus() { return status; }
    public void setStatus(SubscriptionStatus status) { this.status = status; }

    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }

    public Instant getActivatedAt() { return activatedAt; }
    public void setActivatedAt(Instant activatedAt) { this.activatedAt = activatedAt; }
}
