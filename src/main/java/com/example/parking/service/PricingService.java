package com.example.parking.service;

import com.example.parking.model.VehicleType;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
public class PricingService {
    public double calculateFee(VehicleType vehicleType, LocalDateTime entryTime, LocalDateTime exitTime) {
        long hours = Duration.between(entryTime, exitTime).toHours();
        if (hours < 1) {
            hours = 1;
        }

        return switch (vehicleType) {
            case MOTORCYCLE -> Math.min(10.0, hours * 1.0);
            case CAR -> Math.min(20.0, hours * 2.0 + 1.0);
            case BUS -> Math.min(40.0, hours * 4.0 + 3.0);
        };
    }
}
