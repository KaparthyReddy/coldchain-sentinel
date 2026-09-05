package com.coldchainsentinel.repository;

import com.coldchainsentinel.model.TemperatureReading;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TemperatureReadingRepository extends JpaRepository<TemperatureReading, Long> {
    List<TemperatureReading> findByShipmentIdOrderByRecordedAtAsc(Long shipmentId);
}
