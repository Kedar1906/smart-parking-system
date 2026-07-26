package com.example.parking.repository;

import com.example.parking.model.ParkingSpot;
import com.example.parking.model.SpotStatus;
import com.example.parking.model.VehicleType;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ParkingSpotRepository extends JpaRepository<ParkingSpot, Long> {
    List<ParkingSpot> findByStatusAndCompatibleVehicleType(SpotStatus status, VehicleType compatibleVehicleType);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select s from ParkingSpot s where s.status = :status")
    List<ParkingSpot> findAvailableSpotsForUpdate(@Param("status") SpotStatus status);

    List<ParkingSpot> findByStatus(SpotStatus status);
}
