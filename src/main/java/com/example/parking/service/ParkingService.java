package com.example.parking.service;

import com.example.parking.model.*;
import com.example.parking.repository.ParkingSpotRepository;
import com.example.parking.repository.ParkingTransactionRepository;
import com.example.parking.repository.VehicleRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class ParkingService {
    private final ParkingSpotRepository parkingSpotRepository;
    private final VehicleRepository vehicleRepository;
    private final ParkingTransactionRepository transactionRepository;
    private final PricingService pricingService;

    public ParkingService(ParkingSpotRepository parkingSpotRepository,
                          VehicleRepository vehicleRepository,
                          ParkingTransactionRepository transactionRepository,
                          PricingService pricingService) {
        this.parkingSpotRepository = parkingSpotRepository;
        this.vehicleRepository = vehicleRepository;
        this.transactionRepository = transactionRepository;
        this.pricingService = pricingService;
    }

    @Transactional
    public ParkingTransaction checkIn(String plateNumber, VehicleType vehicleType) {
        Vehicle vehicle = vehicleRepository.findByPlateNumber(plateNumber).orElseGet(() -> {
            Vehicle newVehicle = new Vehicle();
            newVehicle.setPlateNumber(plateNumber);
            newVehicle.setVehicleType(vehicleType);
            return vehicleRepository.save(newVehicle);
        });

        Optional<ParkingTransaction> activeTransaction = transactionRepository.findByVehicleIdAndStatus(vehicle.getId(), TransactionStatus.ACTIVE);
        if (activeTransaction.isPresent()) {
            throw new IllegalStateException("Vehicle already parked");
        }

        List<ParkingSpot> spots = parkingSpotRepository.findAvailableSpotsForUpdate(SpotStatus.AVAILABLE);
        ParkingSpot chosenSpot = spots.stream()
                .filter(spot -> spot.getCompatibleVehicleType() == vehicleType)
                .findFirst()
                .or(() -> spots.stream().filter(spot -> spot.getCompatibleVehicleType() == null).findFirst())
                .orElseThrow(() -> new IllegalStateException("No available spots"));

        chosenSpot.setStatus(SpotStatus.OCCUPIED);
        parkingSpotRepository.save(chosenSpot);

        ParkingTransaction transaction = new ParkingTransaction();
        transaction.setVehicle(vehicle);
        transaction.setSpot(chosenSpot);
        transaction.setEntryTime(LocalDateTime.now());
        transaction.setStatus(TransactionStatus.ACTIVE);
        return transactionRepository.save(transaction);
    }

    @Transactional
    public ParkingTransaction checkOut(String plateNumber) {
        Vehicle vehicle = vehicleRepository.findByPlateNumber(plateNumber)
                .orElseThrow(() -> new IllegalArgumentException("Vehicle not found"));

        ParkingTransaction transaction = transactionRepository.findByVehicleIdAndStatus(vehicle.getId(), TransactionStatus.ACTIVE)
                .orElseThrow(() -> new IllegalStateException("Vehicle is not parked"));

        transaction.setExitTime(LocalDateTime.now());
        transaction.setFeeAmount(pricingService.calculateFee(vehicle.getVehicleType(), transaction.getEntryTime(), transaction.getExitTime()));
        transaction.setStatus(TransactionStatus.COMPLETED);

        ParkingSpot spot = transaction.getSpot();
        spot.setStatus(SpotStatus.AVAILABLE);
        parkingSpotRepository.save(spot);

        return transactionRepository.save(transaction);
    }

    public List<ParkingSpot> getParkingSlots() {
        return parkingSpotRepository.findAll();
    }

    public java.util.Map<String, Object> getParkingStatistics() {
        List<ParkingSpot> allSpots = parkingSpotRepository.findAll();
        long availableCount = allSpots.stream().filter(s -> s.getStatus() == SpotStatus.AVAILABLE).count();
        long filledCount = allSpots.stream().filter(s -> s.getStatus() == SpotStatus.OCCUPIED).count();
        
        java.util.Map<String, Object> stats = new java.util.HashMap<>();
        stats.put("totalSpots", allSpots.size());
        stats.put("availableSpots", availableCount);
        stats.put("filledSpots", filledCount);
        stats.put("occupancyRate", allSpots.size() > 0 ? String.format("%.2f%%", (filledCount * 100.0) / allSpots.size()) : "0%");
        
        return stats;
    }
}
