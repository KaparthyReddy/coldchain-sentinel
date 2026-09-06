package com.coldchainsentinel.service;

import com.coldchainsentinel.dto.CheckoutResponse;
import com.coldchainsentinel.dto.SubscriptionRequest;
import com.coldchainsentinel.exception.ResourceNotFoundException;
import com.coldchainsentinel.exception.ValidationException;
import com.coldchainsentinel.model.Subscription;
import com.coldchainsentinel.model.SubscriptionStatus;
import com.coldchainsentinel.model.User;
import com.coldchainsentinel.repository.SubscriptionRepository;
import com.razorpay.PaymentLink;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;

/**
 * Wraps Razorpay's Payment Links API: creates a hosted checkout link for a
 * subscription purchase, and verifies + processes the webhook events
 * Razorpay sends back when that link is paid.
 */
@Service
public class RazorpayService {

    private final SubscriptionRepository subscriptionRepository;
    private final String keyId;
    private final String keySecret;
    private final String webhookSecret;

    public RazorpayService(SubscriptionRepository subscriptionRepository,
                            @Value("${razorpay.key-id}") String keyId,
                            @Value("${razorpay.key-secret}") String keySecret,
                            @Value("${razorpay.webhook-secret}") String webhookSecret) {
        this.subscriptionRepository = subscriptionRepository;
        this.keyId = keyId;
        this.keySecret = keySecret;
        this.webhookSecret = webhookSecret;
    }

    public CheckoutResponse createPaymentLink(User user, SubscriptionRequest request) {
        try {
            RazorpayClient client = new RazorpayClient(keyId, keySecret);

            long amountInPaise = Math.round(request.getAmountInRupees() * 100);

            JSONObject linkRequest = new JSONObject();
            linkRequest.put("amount", amountInPaise);
            linkRequest.put("currency", "INR");
            linkRequest.put("description", "ColdChain Sentinel - " + request.getPlanName());
            linkRequest.put("customer", new JSONObject().put("name", user.getUsername()));
            linkRequest.put("notify", new JSONObject().put("sms", false).put("email", false));
            linkRequest.put("reminder_enable", false);

            PaymentLink paymentLink = client.paymentLink.create(linkRequest);
            String linkId = paymentLink.get("id");
            String shortUrl = paymentLink.get("short_url");

            Subscription subscription = new Subscription(user, request.getPlanName(), amountInPaise, linkId);
            subscriptionRepository.save(subscription);

            return new CheckoutResponse(linkId, shortUrl, request.getPlanName(), SubscriptionStatus.PENDING);
        } catch (RazorpayException e) {
            throw new ValidationException("Failed to create payment link: " + e.getMessage());
        }
    }

    public boolean verifySignature(String payload, String signature) {
        try {
            return Utils.verifyWebhookSignature(payload, signature, webhookSecret);
        } catch (RazorpayException e) {
            return false;
        }
    }

    /**
     * Processes a verified webhook payload. Expects Razorpay's
     * "payment_link.paid" event shape.
     */
    public void handleWebhookEvent(String payload) {
        JSONObject event = new JSONObject(payload);
        String eventType = event.optString("event", "");

        if (!"payment_link.paid".equals(eventType)) {
            return; // ignore events we don't act on
        }

        JSONObject paymentLinkEntity = event
                .getJSONObject("payload")
                .getJSONObject("payment_link")
                .getJSONObject("entity");

        String linkId = paymentLinkEntity.getString("id");

        Subscription subscription = subscriptionRepository.findByRazorpayPaymentLinkId(linkId)
                .orElseThrow(() -> new ResourceNotFoundException("No subscription found for payment link: " + linkId));

        subscription.setStatus(SubscriptionStatus.ACTIVE);
        subscription.setActivatedAt(Instant.now());
        subscriptionRepository.save(subscription);
    }
}
