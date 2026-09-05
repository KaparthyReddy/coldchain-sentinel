package com.coldchainsentinel.repository;

import com.coldchainsentinel.model.Alert;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlertRepository extends JpaRepository<Alert, Long> {
    List<Alert> findByResolvedFalse();
    List<Alert> findByShipmentId(Long shipmentId);
}
