package com.example.parking.controller;

import com.example.parking.model.ParkingSpot;
import com.example.parking.model.ParkingTransaction;
import com.example.parking.model.VehicleType;
import com.example.parking.service.ParkingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/parking")
public class ParkingController {
    private final ParkingService parkingService;

    public ParkingController(ParkingService parkingService) {
        this.parkingService = parkingService;
    }

    @PostMapping("/check-in")
    public ResponseEntity<ParkingTransaction> checkIn(@RequestParam String plateNumber, @RequestParam VehicleType vehicleType) {
        return ResponseEntity.ok(parkingService.checkIn(plateNumber, vehicleType));
    }

    @PostMapping("/check-out")
    public ResponseEntity<ParkingTransaction> checkOut(@RequestParam String plateNumber) {
        return ResponseEntity.ok(parkingService.checkOut(plateNumber));
    }

    @GetMapping("/slots")
    public ResponseEntity<List<ParkingSpot>> getParkingSlots() {
        return ResponseEntity.ok(parkingService.getParkingSlots());
    }

    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getParkingStatistics() {
        return ResponseEntity.ok(parkingService.getParkingStatistics());
    }
}
