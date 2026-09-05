package com.coldchainsentinel.repository;

import com.coldchainsentinel.model.Shipment;
import com.coldchainsentinel.model.ShipmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ShipmentRepository extends JpaRepository<Shipment, Long> {
    List<Shipment> findByStatus(ShipmentStatus status);
}
