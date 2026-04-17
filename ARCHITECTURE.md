# 🚇 Namma Metro - Architecture & Design Documentation

## 📋 Table of Contents
1. [System Architecture Overview](#system-architecture-overview)
2. [SOLID Principles Applied](#solid-principles-applied)
3. [GRASP Patterns Applied](#grasp-patterns-applied)
4. [Design Patterns Used](#design-patterns-used)
5. [Entity Relationships](#entity-relationships)
6. [Data Flow Diagrams](#data-flow-diagrams)
7. [API Usage Guide](#api-usage-guide)
8. [Code Examples](#code-examples)

---

## System Architecture Overview

### 🎯 Core Concepts

The redesigned Namma Metro system implements a **clean, layered architecture** with clear separation of concerns:

```
┌─────────────────────────────────────┐
│      REST Controllers               │ ← API Layer (Request/Response)
├─────────────────────────────────────┤
│      Services (Business Logic)      │ ← Service Layer (Use Cases)
├─────────────────────────────────────┤
│      Repositories (Data Access)     │ ← Repository Layer (Abstraction)
├─────────────────────────────────────┤
│      Models (Domain Objects)        │ ← Domain Layer (Entities)
├─────────────────────────────────────┤
│      Database                       │ ← Persistence Layer
└─────────────────────────────────────┘
```

### 📊 New Entities Created

| Entity | Purpose | Key Responsibility |
|--------|---------|-------------------|
| **Route** | Metro line with multiple stations | Container for stations, distance tracking |
| **Station** | Individual metro station | Location on route, cumulative distance, station user assignment |
| **UserRole** | Enum for user types | Role-based access control (ADMIN, REGULAR_USER, STATION_USER) |
| **RegularUser** | Regular passenger profile | Wallet balance, loyalty points, fare deduction |
| **StationUser** | Station staff profile | Duty management, department, employee tracking |
| **Distance** | Value object | Distance calculation between stations |
| **PricingConfiguration** | Admin pricing setup | Strategy type, rates, minimums |
| **PricingStrategy** (Interface) | Pricing algorithms | Abstract strategy for fare calculation |

---

## SOLID Principles Applied

### 1. **S - Single Responsibility Principle** ✅

Each class has a **single, well-defined responsibility**:

```
RouteService          → Creates and manages routes
StationService        → Manages stations on routes
PricingService        → Calculates fares using strategies
RegularUserService    → Manages regular user profiles
StationUserService    → Manages station staff
TicketService         → Books and manages tickets
```

**Benefit**: Code is easier to understand, test, and modify.

---

### 2. **O - Open/Closed Principle** ✅

The system is **open for extension, closed for modification**:

#### Example: Pricing Strategies
```java
// New pricing strategies can be added WITHOUT changing existing code

interface PricingStrategy {
    Double calculateFare(Station from, Station to);
}

class DistanceBasedPricingStrategy implements PricingStrategy { ... }
class FlatRatePricingStrategy implements PricingStrategy { ... }
class PeakHourPricingStrategy implements PricingStrategy { ... }
class NewPricingStrategy implements PricingStrategy { ... } // Future

// Add new strategy in PricingService factory method - no other changes needed!
```

**Benefit**: Easy to add new pricing strategies without breaking existing code.

---

### 3. **L - Liskov Substitution Principle** ✅

Subtypes can be **used interchangeably**:

```java
// All pricing strategies can be used interchangeably
PricingStrategy strategy;

if (config.strategyType.equals("DISTANCE_BASED")) {
    strategy = new DistanceBasedPricingStrategy(...);
} else if (config.strategyType.equals("FLAT_RATE")) {
    strategy = new FlatRatePricingStrategy(...);
}

// Same interface, different behavior
Double fare = strategy.calculateFare(from, to);
```

**Benefit**: Polymorphism enables flexible algorithm selection.

---

### 4. **I - Interface Segregation Principle** ✅

Clients depend on **specific, focused interfaces**:

```java
// Instead of one bloated UserService, we have specialized services

RegularUserService      // Only user-related methods
StationUserService      // Only staff-related methods
PricingService          // Only pricing-related methods
RouteService            // Only route-related methods
TicketService           // Only ticket-related methods
```

**Benefit**: Services have focused responsibilities, easier to maintain.

---

### 5. **D - Dependency Inversion Principle** ✅

Depend on **abstractions, not concrete implementations**:

```java
@Service
public class TicketService {
    // Depends on abstraction (interface), not concrete class
    private final PricingService pricingService;
    
    public TicketService(PricingService pricingService) {
        this.pricingService = pricingService; // Injected abstraction
    }
    
    public Ticket bookTicket(...) {
        // Uses PricingService abstraction
        Double fare = pricingService.calculateFare(from, to);
    }
}
```

**Benefit**: Easy to test with mock implementations, flexible substitution.

---

## GRASP Patterns Applied

### 1. **Creator Pattern** ✅

Services create domain objects:

```java
@Service
public class RegularUserService {
    public RegularUser registerRegularUser(User user, String phoneNumber) {
        // Service CREATES RegularUser
        RegularUser regularUser = new RegularUser(user, phoneNumber);
        regularUser.setWalletBalance(0.0);
        regularUser.setLoyaltyPoints(0);
        return regularUserRepository.save(regularUser);
    }
}

@Service
public class RouteService {
    public Route createRoute(String name, String description) {
        // Service CREATES Route
        Route route = new Route(name, description);
        return routeRepository.save(route);
    }
}
```

**Benefit**: Centralized object creation logic, easier to enforce invariants.

---

### 2. **Information Expert Pattern** ✅

Objects that **know the most about a responsibility** handle it:

```java
// Distance knows how to calculate distance between stations
public class Distance {
    public static Distance between(Station from, Station to) {
        Double diff = Math.abs(to.getCumulativeDistance() - 
                              from.getCumulativeDistance());
        return new Distance(diff);
    }
}

// PricingService knows how to calculate fares
@Service
public class PricingService {
    public Double calculateFare(Long fromStationId, Long toStationId) {
        // PricingService is the expert on pricing
        Station from = stationRepository.findById(fromStationId);
        Station to = stationRepository.findById(toStationId);
        PricingStrategy strategy = getActivePricingStrategy();
        return strategy.calculateFare(from, to);
    }
}

// RegularUserService knows about user wallets
@Service
public class RegularUserService {
    public boolean deductFare(Long userId, Double fare) {
        // RegularUserService is expert on user finances
        RegularUser user = regularUserRepository.findById(userId);
        return user.deductFare(fare); // Delegates to user object
    }
}
```

**Benefit**: Responsibilities are assigned to objects that have the necessary information.

---

### 3. **Low Coupling, High Cohesion** ✅

Services have **minimal dependencies** and **focused responsibilities**:

```
RouteService ──┐
               ├──→ RouteRepository (minimal coupling)
               ├──→ StationRepository
               
PricingService ──┐
                 ├──→ PricingConfigurationRepository (minimal coupling)
                 ├──→ StationRepository
                 
TicketService ──┐
                ├──→ TicketRepository (minimal coupling)
                ├──→ PricingService (indirect dependency)
                ├──→ RegularUserService
```

**Benefit**: Services can be tested independently, changes in one don't affect others.

---

### 4. **Polymorphism Pattern** ✅

Use object types to handle **alternatives**:

```java
// Different pricing strategies implement same interface
public interface PricingStrategy {
    Double calculateFare(Station from, Station to);
}

// Factory method handles polymorphism
private PricingStrategy createPricingStrategy(PricingConfiguration config) {
    switch (config.getStrategyType()) {
        case "DISTANCE_BASED":
            return new DistanceBasedPricingStrategy(...);
        case "FLAT_RATE":
            return new FlatRatePricingStrategy(...);
        case "PEAK_HOUR":
            return new PeakHourPricingStrategy(...);
    }
}
```

**Benefit**: Easy to add new behavior without modifying existing code.

---

## Design Patterns Used

### 🏗️ 1. Strategy Pattern

**Problem**: Different pricing algorithms (distance-based, flat-rate, peak-hour)

**Solution**: Define strategy interface, implement concrete strategies

```
PricingStrategy (interface)
    ├── DistanceBasedPricingStrategy
    ├── FlatRatePricingStrategy
    └── PeakHourPricingStrategy
```

### 🏗️ 2. Factory Pattern

**Problem**: Creating the right PricingStrategy based on config type

**Solution**: Factory method in PricingService

```java
private PricingStrategy createPricingStrategy(PricingConfiguration config) {
    switch (config.getStrategyType()) { ... }
}
```

### 🏗️ 3. Value Object Pattern

**Problem**: Representing Distance without an entity

**Solution**: Immutable Distance value object

```java
public class Distance {
    private final Double kilometers;
    
    public Distance(Double kilometers) {
        if (kilometers < 0) throw new Exception("Invalid distance");
        this.kilometers = kilometers;
    }
}
```

### 🏗️ 4. Repository Pattern

**Problem**: Abstract data access logic

**Solution**: Repository interfaces hide database details

```java
@Repository
public interface RouteRepository extends JpaRepository<Route, Long> {
    Optional<Route> findByName(String name);
}
```

### 🏗️ 5. Observer Pattern (Existing)

**Problem**: Notify users about train updates

**Solution**: Observer pattern in NotificationService

```
Observer (interface)
    └── UserObserver, StationObserver
```

### 🏗️ 6. State Pattern (Existing)

**Problem**: Train status transitions

**Solution**: State objects handle status changes

```
TrainState (interface)
    ├── ScheduledState
    ├── RunningState
    ├── DelayedState
    └── CancelledState
```

---

## Entity Relationships

### 📊 ER Diagram

```
Route (1) ──────────── (N) Station
  ├─ id                  ├─ id
  ├─ name                ├─ name
  └─ description         ├─ code
                         ├─ order
                         ├─ distanceToNext
                         └─ cumulativeDistance

Station (1) ──────────── (N) Train
                         ├─ id
                         ├─ name
                         ├─ capacity
                         └─ status

Train (1) ──────────── (N) Ticket
                       ├─ id
                       ├─ passengerName
                       ├─ fare
                       └─ status

User (1) ──────────── (1) RegularUser
  ├─ id                ├─ id
  ├─ name              ├─ phoneNumber
  ├─ email             ├─ walletBalance
  └─ password          └─ loyaltyPoints

User (1) ──────────── (1) StationUser
  ├─ id                ├─ id
  ├─ name              ├─ employeeId
  ├─ email             ├─ department
  └─ password          ├─ shiftTiming
                       └─ onDuty

Station (1) ──────────── (N) StationUser
  (managed by)         (manages)

RegularUser (1) ──────────── (N) Ticket
  (books)              (booked by)

Ticket (N) ──────────── (1) Station
  (from/to)            (source/destination)

PricingConfiguration
  ├─ id
  ├─ name
  ├─ strategyType
  ├─ baseRatePerKm
  ├─ minimumFare
  └─ isActive
```

---

## Data Flow Diagrams

### 1. Ticket Booking Flow

```
┌─────────────────────────────────────┐
│  Regular User (Frontend)            │
│  Book Ticket Request                │
└────────────┬────────────────────────┘
             │
             ▼
┌─────────────────────────────────────┐
│  TicketController                   │
│  POST /api/tickets/book             │
└────────────┬────────────────────────┘
             │
             ▼
┌─────────────────────────────────────┐
│  TicketService.bookTicket()         │
│  1. Validate inputs                 │
│  2. Fetch entities                  │
└────────────┬────────────────────────┘
             │
             ▼
┌─────────────────────────────────────┐
│  PricingService.calculateFare()     │
│  1. Get active strategy             │
│  2. Calculate distance              │
│  3. Apply strategy                  │
└────────────┬────────────────────────┘
             │
             ▼
┌─────────────────────────────────────┐
│  RegularUserService                 │
│  1. Check wallet balance            │
│  2. Deduct fare                     │
│  3. Add loyalty points              │
└────────────┬────────────────────────┘
             │
             ▼
┌─────────────────────────────────────┐
│  TicketRepository.save()            │
│  Create ticket record               │
└────────────┬────────────────────────┘
             │
             ▼
┌─────────────────────────────────────┐
│  Notification                       │
│  Notify users via Observer          │
└─────────────────────────────────────┘
```

### 2. Pricing Configuration Flow

```
Admin Sets Pricing
       │
       ▼
PricingController
       │
       ▼
PricingService.createDistanceBasedPricing()
       │
       ▼
Create PricingConfiguration
       │
       ▼
PricingController.activatePricingConfiguration()
       │
       ▼
Set isActive = true
       │
       ▼
PricingService.getActivePricingStrategy()
       │
       ▼
Use DistanceBasedPricingStrategy for fare calculation
```

### 3. Train Route Setup Flow

```
Admin Creates Route
       │
       ▼
RouteController.createRoute()
       │
       ▼
RouteService.createRoute()
       │
       ▼
Create Route Entity
       │
       ▼
Admin Adds Stations
       │
       ▼
RouteController.addStationToRoute()
       │
       ▼
RouteService.addStationToRoute()
       │
       ▼
Create Station + Calculate Cumulative Distance
       │
       ▼
Admin Creates Train
       │
       ▼
TrainController.createTrain()
       │
       ▼
TrainService.addTrain()
       │
       ▼
Create Train linked to Route
```

---

## API Usage Guide

### 🚀 Complete Workflow Examples

#### Step 1: Admin Creates Route with Stations

```bash
# Create Route (Purple Line)
curl -X POST http://localhost:8080/api/routes \
  -H "Content-Type: application/json" \
  -d '{"name": "Purple Line", "description": "North-South corridor"}'

# Response: Route created with id=1

# Add Stations to Route
curl -X POST http://localhost:8080/api/routes/1/stations \
  -H "Content-Type: application/json" \
  -d '{
    "stationName": "Majestic",
    "stationCode": "MG",
    "order": 1,
    "distanceToNext": 0
  }'

curl -X POST http://localhost:8080/api/routes/1/stations \
  -H "Content-Type: application/json" \
  -d '{
    "stationName": "Vidhana Soudha",
    "stationCode": "VS",
    "order": 2,
    "distanceToNext": 2.5
  }'

curl -X POST http://localhost:8080/api/routes/1/stations \
  -H "Content-Type: application/json" \
  -d '{
    "stationName": "Vikaramnagar",
    "stationCode": "VK",
    "order": 3,
    "distanceToNext": 3.2
  }'
```

#### Step 2: Admin Creates Pricing Configuration

```bash
# Create Distance-Based Pricing: 5 per km, minimum 10
curl -X POST http://localhost:8080/api/pricing/distance-based \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Standard Distance-Based",
    "baseRatePerKm": 5.0,
    "minimumFare": 10.0,
    "adminId": 1
  }'

# Response: PricingConfiguration created with id=1

# Activate this pricing configuration
curl -X POST http://localhost:8080/api/pricing/configurations/1/activate \
  -H "Content-Type: application/json"
```

#### Step 3: Admin Creates Trains

```bash
# Create Train on the Route
curl -X POST http://localhost:8080/api/trains \
  -H "Content-Type: application/json" \
  -d '{
    "trainName": "Metro Line 1 - Train 01",
    "routeId": 1,
    "capacity": 500,
    "departureTime": "06:00",
    "arrivalTime": "22:00"
  }'

# Response: Train created with id=1
```

#### Step 4: Register Regular User

```bash
# Register Regular Passenger
curl -X POST http://localhost:8080/api/users/regular/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "John Doe",
    "email": "john@example.com",
    "password": "secure123",
    "phoneNumber": "9876543210"
  }'

# Response: RegularUser created with id=1 (userId=1)

# Add Wallet Balance
curl -X POST http://localhost:8080/api/users/regular/1/wallet/add \
  -H "Content-Type: application/json" \
  -d '{"amount": 500.0}'
```

#### Step 5: Book Ticket

```bash
# First, check fare between stations
curl -X GET "http://localhost:8080/api/pricing/calculate?from=1&to=3" \
  -H "Content-Type: application/json"
# Response: { "from_station_id": 1, "to_station_id": 3, "fare": 30.0 }

# Check if user can afford
curl -X GET "http://localhost:8080/api/users/regular/1/can-afford?fare=30.0" \
  -H "Content-Type: application/json"
# Response: { "fare": 30.0, "balance": 500.0, "can_afford": true, "shortage": 0 }

# Book Ticket
curl -X POST http://localhost:8080/api/tickets/book \
  -H "Content-Type: application/json" \
  -d '{
    "regularUserId": 1,
    "trainId": 1,
    "sourceStationId": 1,
    "destinationStationId": 3,
    "passengerName": "John Doe",
    "travelDate": "2024-12-25"
  }'

# Response: Ticket created with id=1
# User's wallet balance: 500 - 30 = 470
# User's loyalty points: 3 (30/10 = 3)
```

#### Step 6: Register Station Staff

```bash
# Register Station User
curl -X POST http://localhost:8080/api/users/station/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Ram Kumar",
    "email": "ram@metro.com",
    "password": "secure123",
    "stationId": 1,
    "employeeId": "EMP001",
    "department": "Operations"
  }'

# Response: StationUser created with id=1

# Start Duty
curl -X POST http://localhost:8080/api/users/station/1/start-duty \
  -H "Content-Type: application/json"

# Check Duty Status
curl -X GET http://localhost:8080/api/users/station/1/duty-status \
  -H "Content-Type: application/json"
# Response: { "employee_id": "EMP001", "on_duty": true, "department": "Operations" }
```

---

## Code Examples

### Example 1: Adding a New Pricing Strategy

```java
// Step 1: Implement PricingStrategy interface
public class SeasonalPricingStrategy implements PricingStrategy {
    private final Double baseRatePerKm;
    private final Double seasonMultiplier; // 1.2 for peak season
    
    public SeasonalPricingStrategy(Double baseRatePerKm, Double seasonMultiplier) {
        this.baseRatePerKm = baseRatePerKm;
        this.seasonMultiplier = seasonMultiplier;
    }
    
    @Override
    public Double calculateFare(Station from, Station to) {
        Distance distance = Distance.between(from, to);
        return distance.getKilometers() * baseRatePerKm * seasonMultiplier;
    }
    
    @Override
    public String getStrategyName() {
        return "Seasonal Pricing";
    }
    
    @Override
    public String getDescription() {
        return "Base rate: " + baseRatePerKm + "/km, Season multiplier: " + 
               seasonMultiplier;
    }
}

// Step 2: Add to PricingService factory method
private PricingStrategy createPricingStrategy(PricingConfiguration config) {
    switch (config.getStrategyType()) {
        case "DISTANCE_BASED":
            return new DistanceBasedPricingStrategy(...);
        case "FLAT_RATE":
            return new FlatRatePricingStrategy(...);
        case "PEAK_HOUR":
            return new PeakHourPricingStrategy(...);
        case "SEASONAL":  // NEW
            return new SeasonalPricingStrategy(...);
        default:
            throw new RuntimeException("Unknown strategy");
    }
}

// That's it! No need to modify PricingService logic or controller
```

### Example 2: Getting User's Ticket History with Fares

```java
// In RegularUserService or new TicketQueryService
public List<Map<String, Object>> getTicketHistoryWithFares(Long regularUserId) {
    RegularUser regularUser = regularUserRepository.findById(regularUserId)
            .orElseThrow(() -> new RuntimeException("User not found"));
    
    List<Ticket> tickets = ticketRepository.findByRegularUser(regularUser);
    
    return tickets.stream()
            .map(ticket -> Map.of(
                "ticketId", ticket.getId(),
                "passengerName", ticket.getPassengerName(),
                "from_station", ticket.getSourceStationName(),
                "to_station", ticket.getDestinationStationName(),
                "distance_km", ticket.getDistance(),
                "fare", ticket.getFare(),
                "discount", ticket.getDiscount(),
                "finalPrice", ticket.getFinalPrice(),
                "status", ticket.getStatus(),
                "bookingDate", ticket.getBookingTime()
            ))
            .collect(Collectors.toList());
}
```

### Example 3: Calculating Revenue Report for a Route

```java
// In ReportService (new service)
public Map<String, Object> getRouteRevenueReport(Long routeId) {
    Route route = routeRepository.findById(routeId)
            .orElseThrow(() -> new RuntimeException("Route not found"));
    
    List<Train> trains = trainRepository.findByRoute(route);
    
    Double totalRevenue = 0.0;
    Integer totalTickets = 0;
    
    for (Train train : trains) {
        List<Ticket> tickets = ticketRepository.findByStatus("USED");
        for (Ticket ticket : tickets) {
            if (ticket.getTrain().getId().equals(train.getId())) {
                totalRevenue += ticket.getFinalPrice();
                totalTickets++;
            }
        }
    }
    
    Double avgFare = totalTickets > 0 ? totalRevenue / totalTickets : 0.0;
    
    return Map.of(
        "route_name", route.getName(),
        "total_revenue", totalRevenue,
        "total_tickets", totalTickets,
        "average_fare", avgFare,
        "trains_on_route", trains.size()
    );
}
```

---

## 🎓 Key Learnings

### Why This Design is Better

1. **Maintainability**: Changes to pricing don't affect tickets or users
2. **Testability**: Each service can be tested independently  
3. **Extensibility**: Add new pricing strategies without modifying existing code
4. **Scalability**: Services can be scaled independently
5. **Reusability**: Services and strategies can be reused in different contexts
6. **Clear Responsibilities**: Each class knows exactly what it's responsible for

### Testing Approach

```java
@ExtendWith(MockitoExtension.class)
public class PricingServiceTest {
    
    @Mock
    private PricingConfigurationRepository configRepo;
    
    @Mock
    private StationRepository stationRepo;
    
    private PricingService pricingService;
    
    @BeforeEach
    public void setup() {
        pricingService = new PricingService(configRepo, stationRepo);
    }
    
    @Test
    public void testDistanceBasedPricingCalculation() {
        // Create mock stations
        Station from = createMockStation(0.0);
        Station to = createMockStation(5.0);
        
        // Create strategy
        PricingStrategy strategy = new DistanceBasedPricingStrategy(5.0, 10.0);
        
        // Test
        Double fare = strategy.calculateFare(from, to);
        
        // Assert: 5 km * 5/km = 25
        assertEquals(25.0, fare);
    }
}
```

---

## 🚀 Next Steps

1. **Implement Caching**: Cache active pricing configuration
2. **Add Reporting**: Revenue, usage, and passenger statistics
3. **Implement Search**: Find routes by stations, find cheap fares
4. **Add Authentication**: Secure endpoints with JWT/OAuth2
5. **Implement Notifications**: Real-time updates via WebSocket
6. **Add Performance Monitoring**: Track slow queries, optimize N+1 problems

---

**Last Updated**: April 2024  
**Architecture Version**: 2.0  
**Design Patterns**: 6  
**SOLID Principles**: 5/5 Applied  
**GRASP Patterns**: 4/4 Applied  
