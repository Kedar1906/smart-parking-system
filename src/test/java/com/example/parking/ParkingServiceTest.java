package com.example.parking;

import com.example.parking.model.*;
import com.example.parking.repository.ParkingSpotRepository;
import com.example.parking.repository.ParkingTransactionRepository;
import com.example.parking.repository.VehicleRepository;
import com.example.parking.service.ParkingService;
import com.example.parking.service.PricingService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ParkingServiceTest {
    @Autowired
    private ParkingService parkingService;

    @Autowired
    private ParkingSpotRepository parkingSpotRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private ParkingTransactionRepository transactionRepository;

    @Test
    void shouldCheckInAndCheckOutVehicle() {
        ParkingSpot spot = new ParkingSpot();
        spot.setFloor("1");
        spot.setSpotNumber("A1");
        spot.setCompatibleVehicleType(VehicleType.CAR);
        parkingSpotRepository.save(spot);

        ParkingTransaction transaction = parkingService.checkIn("ABC123", VehicleType.CAR);

        assertNotNull(transaction.getId());
        assertEquals(TransactionStatus.ACTIVE, transaction.getStatus());
        assertEquals("ABC123", vehicleRepository.findByPlateNumber("ABC123").orElseThrow().getPlateNumber());

        ParkingTransaction completed = parkingService.checkOut("ABC123");
        assertEquals(TransactionStatus.COMPLETED, completed.getStatus());
        assertTrue(completed.getFeeAmount() >= 0);
    }
}
