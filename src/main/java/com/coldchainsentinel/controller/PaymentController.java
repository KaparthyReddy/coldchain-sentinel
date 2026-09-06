package com.coldchainsentinel.controller;

import com.coldchainsentinel.dto.CheckoutResponse;
import com.coldchainsentinel.dto.SubscriptionRequest;
import com.coldchainsentinel.exception.ValidationException;
import com.coldchainsentinel.model.User;
import com.coldchainsentinel.repository.UserRepository;
import com.coldchainsentinel.service.RazorpayService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.coldchainsentinel.model.Subscription;
import com.coldchainsentinel.repository.SubscriptionRepository;

@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {

    private final RazorpayService razorpayService;
    private final UserRepository userRepository;
    private final SubscriptionRepository subscriptionRepository;

    public PaymentController(RazorpayService razorpayService, UserRepository userRepository,
                              SubscriptionRepository subscriptionRepository) {
        this.razorpayService = razorpayService;
        this.userRepository = userRepository;
        this.subscriptionRepository = subscriptionRepository;
    }

    @PostMapping("/subscribe")
    public CheckoutResponse subscribe(@Valid @RequestBody SubscriptionRequest request, Authentication authentication) {
        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new ValidationException("User not found: " + authentication.getName()));
        return razorpayService.createPaymentLink(user, request);
    }

    /**
     * Razorpay calls this directly - no JWT is sent, so this path is public
     * in SecurityConfig. Authenticity is verified via HMAC signature instead
     * of a bearer token.
     */

    @GetMapping("/subscriptions")
    public java.util.List<Subscription> mySubscriptions(Authentication authentication) {
        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new ValidationException("User not found: " + authentication.getName()));
        return subscriptionRepository.findByUserId(user.getId());
    }
    
    @PostMapping("/webhook")
    public ResponseEntity<String> webhook(@RequestBody String payload,
                                           @RequestHeader("X-Razorpay-Signature") String signature) {
        if (!razorpayService.verifySignature(payload, signature)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid signature");
        }
        razorpayService.handleWebhookEvent(payload);
        return ResponseEntity.ok("OK");
    }
}
