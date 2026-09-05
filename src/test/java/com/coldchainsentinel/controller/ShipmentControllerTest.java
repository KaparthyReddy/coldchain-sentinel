package com.coldchainsentinel.controller;

import com.coldchainsentinel.model.Product;
import com.coldchainsentinel.model.Shipment;
import com.coldchainsentinel.model.StorageUnit;
import com.coldchainsentinel.model.TemperatureReading;
import com.coldchainsentinel.service.ShipmentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ShipmentController.class)
@AutoConfigureMockMvc(addFilters = false)
class ShipmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ShipmentService shipmentService;

    private Shipment shipment;

    @BeforeEach
    void setUp() {
        Product product = new Product("SKU-1", "TestVaccine", -25.0, -15.0, 180);
        product.setId(1L);
        StorageUnit origin = new StorageUnit("Freezer A", "Site 1", "FREEZER");
        origin.setId(2L);

        shipment = new Shipment(product, origin, null);
        shipment.setId(10L);
    }

    @Test
    void createShipmentReturnsMappedResponse() throws Exception {
        when(shipmentService.createShipment(any())).thenReturn(shipment);

        String body = """
                {"productSku": "SKU-1", "originUnitId": 2}
                """;

        mockMvc.perform(post("/api/v1/shipments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.productName").value("TestVaccine"))
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void listAllReturnsShipments() throws Exception {
        when(shipmentService.listAll()).thenReturn(List.of(shipment));

        mockMvc.perform(get("/api/v1/shipments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(10));
    }

    @Test
    void getByIdReturnsShipment() throws Exception {
        when(shipmentService.getById(10L)).thenReturn(shipment);

        mockMvc.perform(get("/api/v1/shipments/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.productName").value("TestVaccine"));
    }

    @Test
    void ingestReadingReturnsSavedReading() throws Exception {
        TemperatureReading reading = new TemperatureReading(shipment, -20.0);
        reading.setId(100L);
        when(shipmentService.ingestReading(eq(10L), eq(-20.0))).thenReturn(reading);

        String body = """
                {"temperatureC": -20.0}
                """;

        mockMvc.perform(post("/api/v1/shipments/10/readings")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.temperatureC").value(-20.0));
    }
}
