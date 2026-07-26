package com.example.parking.repository;

import com.example.parking.model.ParkingTransaction;
import com.example.parking.model.TransactionStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ParkingTransactionRepository extends JpaRepository<ParkingTransaction, Long> {
    Optional<ParkingTransaction> findByVehicleIdAndStatus(Long vehicleId, TransactionStatus status);
}
