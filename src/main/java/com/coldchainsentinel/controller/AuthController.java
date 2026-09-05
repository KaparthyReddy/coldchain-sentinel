package com.coldchainsentinel.controller;

import com.coldchainsentinel.dto.AuthRequest;
import com.coldchainsentinel.dto.AuthResponse;
import com.coldchainsentinel.model.Role;
import com.coldchainsentinel.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register/pharmacist")
    public AuthResponse registerPharmacist(@Valid @RequestBody AuthRequest request) {
        return authService.register(request, Role.PHARMACIST);
    }

    @PostMapping("/register/logistics")
    public AuthResponse registerLogistics(@Valid @RequestBody AuthRequest request) {
        return authService.register(request, Role.LOGISTICS);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody AuthRequest request) {
        return authService.login(request);
    }
}
