package com.coldchainsentinel.controller;

import com.coldchainsentinel.dto.AuthResponse;
import com.coldchainsentinel.model.Role;
import com.coldchainsentinel.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @Test
    void loginReturnsTokenAndRole() throws Exception {
        when(authService.login(any())).thenReturn(new AuthResponse("fake-jwt-token", "alice", Role.PHARMACIST));

        String body = objectMapper.writeValueAsString(new LoginPayload("alice", "password123"));

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("fake-jwt-token"))
                .andExpect(jsonPath("$.username").value("alice"))
                .andExpect(jsonPath("$.role").value("PHARMACIST"));
    }

    @Test
    void registerPharmacistReturnsToken() throws Exception {
        when(authService.register(any(), org.mockito.ArgumentMatchers.eq(Role.PHARMACIST)))
                .thenReturn(new AuthResponse("new-token", "bob", Role.PHARMACIST));

        String body = objectMapper.writeValueAsString(new LoginPayload("bob", "password123"));

        mockMvc.perform(post("/api/v1/auth/register/pharmacist")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("new-token"));
    }

    private record LoginPayload(String username, String password) { }
}
