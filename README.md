# Smart Parking System

A comprehensive Spring Boot backend for a smart parking lot management system that handles vehicle check-in/check-out, intelligent parking spot allocation, and dynamic fee calculation.

## Table of Contents
- [Features](#features)
- [Tech Stack](#tech-stack)
- [Project Structure](#project-structure)
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Running the Application](#running-the-application)
- [API Endpoints](#api-endpoints)
- [Database Configuration](#database-configuration)
- [Data Models](#data-models)
- [Running Tests](#running-tests)
- [Architecture & Features](#architecture--features)

## Features

✅ **Vehicle Check-in/Check-out** - Track vehicle parking sessions with entry and exit times  
✅ **Intelligent Spot Allocation** - Automatically assigns parking spots based on vehicle type and availability  
✅ **Size-based Parking** - Supports different vehicle types (CAR, BIKE, TRUCK) with compatible parking spots  
✅ **Dynamic Fee Calculation** - Calculates parking fees based on vehicle type and duration  
✅ **Concurrency-Safe Operations** - Uses database locks to ensure consistent spot assignments  
✅ **Parking Statistics** - Real-time availability and occupancy metrics  
✅ **Transaction Tracking** - Maintains complete history of all parking transactions  

## Tech Stack

- **Framework**: Spring Boot 3.3.2
- **Language**: Java 17
- **Database**: H2 (In-memory, configurable to other databases)
- **ORM**: Spring Data JPA / Hibernate
- **Build Tool**: Maven
- **Testing**: JUnit 5, Spring Boot Test

## Project Structure

```
smart-parking-system/
├── src/
│   ├── main/
│   │   ├── java/com/example/parking/
│   │   │   ├── SmartParkingApplication.java      # Main Spring Boot entry point
│   │   │   ├── controller/
│   │   │   │   └── ParkingController.java        # REST API endpoints
│   │   │   ├── service/
│   │   │   │   ├── ParkingService.java           # Business logic
│   │   │   │   └── PricingService.java           # Fee calculation logic
│   │   │   ├── repository/
│   │   │   │   ├── ParkingSpotRepository.java    # Parking spot queries
│   │   │   │   ├── ParkingTransactionRepository.java # Transaction queries
│   │   │   │   └── VehicleRepository.java        # Vehicle queries
│   │   │   ├── model/
│   │   │   │   ├── ParkingSpot.java              # Parking spot entity
│   │   │   │   ├── ParkingTransaction.java       # Transaction entity
│   │   │   │   ├── Vehicle.java                  # Vehicle entity
│   │   │   │   ├── SpotStatus.java               # Status enum (AVAILABLE, OCCUPIED)
│   │   │   │   ├── TransactionStatus.java        # Transaction enum (ACTIVE, COMPLETED)
│   │   │   │   └── VehicleType.java              # Vehicle type enum (CAR, BIKE, TRUCK)
│   │   │   └── config/
│   │   │       └── DataSeeder.java               # Initial data setup
│   │   └── resources/
│   │       └── application.properties             # Configuration
│   └── test/
│       └── java/com/example/parking/
│           └── ParkingServiceTest.java           # Unit tests
├── pom.xml                                        # Maven configuration
└── README.md                                      # This file
```

## Prerequisites

- **Java 17** or higher
- **Maven 3.6+**
- **IDE**: IntelliJ IDEA, Eclipse, or VS Code

## Installation

### 1. Clone/Download the Project
```bash
cd smart-parking-system
```

### 2. Install Dependencies
```bash
mvn clean install
```

## Running the Application

### Option 1: Maven Command Line
```bash
mvn spring-boot:run
```

### Option 2: IntelliJ IDEA
1. Open [SmartParkingApplication.java](src/main/java/com/example/parking/SmartParkingApplication.java)
2. Click the green **▶ Run** button next to the class definition
3. Or use **Shift+F10** keyboard shortcut

### Option 3: Maven Build & Run JAR
```bash
mvn clean package
java -jar target/smart-parking-system-1.0.0.jar
```

### Option 4: VS Code Terminal
```bash
mvn spring-boot:run
```

**Default Server**: http://localhost:8080  
**H2 Console**: http://localhost:8080/h2-console

## API Endpoints

### 1. Check-In Vehicle
**Endpoint**: `POST /parking/check-in`

**Parameters**:
- `plateNumber` (required): Vehicle plate number (e.g., "ABC123")
- `vehicleType` (required): Vehicle type - `CAR`, `BIKE`, or `TRUCK`

**Example Request**:
```bash
curl -X POST "http://localhost:8080/parking/check-in?plateNumber=ABC123&vehicleType=CAR"
```

**Example Response**:
```json
{
  "id": 1,
  "vehicle": {
    "id": 1,
    "plateNumber": "ABC123",
    "vehicleType": "CAR"
  },
  "spot": {
    "id": 5,
    "floor": "A",
    "spotNumber": "A-05",
    "compatibleVehicleType": "CAR",
    "status": "OCCUPIED"
  },
  "entryTime": "2024-07-26T10:30:45",
  "exitTime": null,
  "status": "ACTIVE",
  "feeAmount": null
}
```

---

### 2. Check-Out Vehicle
**Endpoint**: `POST /parking/check-out`

**Parameters**:
- `plateNumber` (required): Vehicle plate number (e.g., "ABC123")

**Example Request**:
```bash
curl -X POST "http://localhost:8080/parking/check-out?plateNumber=ABC123"
```

**Example Response**:
```json
{
  "id": 1,
  "vehicle": {
    "id": 1,
    "plateNumber": "ABC123",
    "vehicleType": "CAR"
  },
  "spot": {
    "id": 5,
    "floor": "A",
    "spotNumber": "A-05",
    "compatibleVehicleType": "CAR",
    "status": "AVAILABLE"
  },
  "entryTime": "2024-07-26T10:30:45",
  "exitTime": "2024-07-26T11:45:30",
  "status": "COMPLETED",
  "feeAmount": 50.00
}
```

---

### 3. Get All Parking Slots
**Endpoint**: `GET /parking/slots`

**Example Request**:
```bash
curl -X GET "http://localhost:8080/parking/slots"
```

**Example Response**:
```json
[
  {
    "id": 1,
    "floor": "A",
    "spotNumber": "A-01",
    "compatibleVehicleType": "CAR",
    "status": "AVAILABLE"
  },
  {
    "id": 2,
    "floor": "A",
    "spotNumber": "A-02",
    "compatibleVehicleType": "BIKE",
    "status": "OCCUPIED"
  },
  {
    "id": 3,
    "floor": "B",
    "spotNumber": "B-01",
    "compatibleVehicleType": "TRUCK",
    "status": "AVAILABLE"
  }
]
```

---

### 4. Get Parking Statistics
**Endpoint**: `GET /parking/statistics`

**Example Request**:
```bash
curl -X GET "http://localhost:8080/parking/statistics"
```

**Example Response**:
```json
{
  "totalSpots": 50,
  "availableSpots": 35,
  "filledSpots": 15,
  "occupancyRate": "30.00%"
}
```

---

## Database Configuration

The application uses H2 in-memory database by default. Configuration is in [application.properties](src/main/resources/application.properties):

```properties
spring.datasource.url=jdbc:h2:mem:parkingdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true
spring.h2.console.enabled=true
```

**To switch to another database** (e.g., MySQL):
1. Add the database driver dependency to `pom.xml`
2. Update the datasource URL and credentials in `application.properties`

### H2 Console
Access the H2 web console at: http://localhost:8080/h2-console
- **JDBC URL**: `jdbc:h2:mem:parkingdb`
- **Username**: `sa`
- **Password**: (leave empty)

## Data Models

### Vehicle
```java
- id: Long (Primary Key)
- plateNumber: String (Unique)
- vehicleType: VehicleType (CAR, BIKE, TRUCK)
```

### ParkingSpot
```java
- id: Long (Primary Key)
- floor: String (e.g., "A", "B", "C")
- spotNumber: String (e.g., "A-01", "B-05")
- compatibleVehicleType: VehicleType (null = universal)
- status: SpotStatus (AVAILABLE, OCCUPIED)
- version: Long (Optimistic locking)
```

### ParkingTransaction
```java
- id: Long (Primary Key)
- vehicle: Vehicle (Foreign Key)
- spot: ParkingSpot (Foreign Key)
- entryTime: LocalDateTime
- exitTime: LocalDateTime
- status: TransactionStatus (ACTIVE, COMPLETED)
- feeAmount: BigDecimal
```

### Enums
- **SpotStatus**: `AVAILABLE`, `OCCUPIED`
- **TransactionStatus**: `ACTIVE`, `COMPLETED`
- **VehicleType**: `CAR`, `BIKE`, `TRUCK`

## Running Tests

### Run All Tests
```bash
mvn test
```

### Run Specific Test Class
```bash
mvn test -Dtest=ParkingServiceTest
```

### Run in IntelliJ
1. Open [ParkingServiceTest.java](src/test/java/com/example/parking/ParkingServiceTest.java)
2. Click the green **▶** icon next to the test class or method
3. Or press **Ctrl+Shift+F10** to run the current test

## Architecture & Features

### 1. Concurrency Management
- Uses **pessimistic locking** on parking spot queries to prevent race conditions
- Database `@Version` field for optimistic locking on entities

### 2. Fee Calculation
- Implemented in `PricingService`
- Dynamic pricing based on vehicle type and parking duration
- Calculated at check-out

### 3. Data Seeding
- `DataSeeder` initializes parking spots and vehicles on application startup
- Creates spots on different floors with vehicle type compatibility

### 4. Transaction Safety
- All critical operations wrapped with `@Transactional` annotation
- Rollback on exceptions ensures data consistency

### 5. RESTful API Design
- Follows REST conventions
- Query parameters for input
- JSON request/response bodies
- Proper HTTP status codes

## Troubleshooting

**Port already in use?**
```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=8081"
```

**Database locked/reset?**
- H2 in-memory database resets on application restart
- Data is not persisted between restarts

**Tests failing?**
```bash
mvn clean test
```

## Future Enhancements

- Add user authentication & authorization
- Implement payment gateway integration
- Add advanced reporting & analytics
- Support for reserved/valet parking
- Mobile app integration
- Email/SMS notifications
- Parking spot pre-booking

## License

This project is open source and available under the MIT License.
