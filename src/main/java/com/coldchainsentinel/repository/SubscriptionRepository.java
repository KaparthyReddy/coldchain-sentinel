package com.coldchainsentinel.repository;

import com.coldchainsentinel.model.Subscription;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
    Optional<Subscription> findByRazorpayPaymentLinkId(String razorpayPaymentLinkId);
    List<Subscription> findByUserId(Long userId);
}
