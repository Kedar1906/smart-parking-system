package com.example.parking.config;

import com.example.parking.model.ParkingSpot;
import com.example.parking.model.SpotStatus;
import com.example.parking.model.VehicleType;
import com.example.parking.repository.ParkingSpotRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataSeeder {
    @Bean
    CommandLineRunner seedParkingSpots(ParkingSpotRepository parkingSpotRepository) {
        return args -> {
            if (parkingSpotRepository.count() == 0) {
                for (int i = 1; i <= 3; i++) {
                    ParkingSpot motorcycleSpot = new ParkingSpot();
                    motorcycleSpot.setFloor("1");
                    motorcycleSpot.setSpotNumber("M" + i);
                    motorcycleSpot.setCompatibleVehicleType(VehicleType.MOTORCYCLE);
                    motorcycleSpot.setStatus(SpotStatus.AVAILABLE);
                    parkingSpotRepository.save(motorcycleSpot);
                }

                for (int i = 1; i <= 4; i++) {
                    ParkingSpot carSpot = new ParkingSpot();
                    carSpot.setFloor("2");
                    carSpot.setSpotNumber("C" + i);
                    carSpot.setCompatibleVehicleType(VehicleType.CAR);
                    carSpot.setStatus(SpotStatus.AVAILABLE);
                    parkingSpotRepository.save(carSpot);
                }

                ParkingSpot busSpot = new ParkingSpot();
                busSpot.setFloor("3");
                busSpot.setSpotNumber("B1");
                busSpot.setCompatibleVehicleType(VehicleType.BUS);
                busSpot.setStatus(SpotStatus.AVAILABLE);
                parkingSpotRepository.save(busSpot);
            }
        };
    }
}
