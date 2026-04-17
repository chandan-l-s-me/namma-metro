# 🚀 Quick Start Guide - Namma Metro API

## Prerequisites

- Java 17+
- Maven 3.9+
- MySQL or H2 database
- Postman or curl

---

## 1. Run the Application

```bash
cd namma-metro
./mvnw spring-boot:run
```

Application will start on `http://localhost:8080`

---

## 2. API Endpoints Reference

### 📍 Route Management

| Method | Endpoint | Purpose |
|--------|----------|---------|
| POST | `/api/routes` | Create new route |
| GET | `/api/routes` | Get all routes |
| GET | `/api/routes/{id}` | Get route by ID |
| POST | `/api/routes/{id}/stations` | Add station to route |
| GET | `/api/routes/{id}/stations` | Get all stations on route |
| GET | `/api/routes/distance` | Get distance between stations |
| DELETE | `/api/routes/{id}` | Delete route |

### 🚆 Train Management

| Method | Endpoint | Purpose |
|--------|----------|---------|
| POST | `/api/trains` | Create train |
| GET | `/api/trains` | Get all trains |
| GET | `/api/trains/{id}` | Get train by ID |
| GET | `/api/trains/route/{id}` | Get trains by route |
| PUT | `/api/trains/{id}/status` | Update train status |
| DELETE | `/api/trains/{id}` | Delete train |

### 💰 Pricing Management

| Method | Endpoint | Purpose |
|--------|----------|---------|
| POST | `/api/pricing/distance-based` | Create distance-based pricing |
| POST | `/api/pricing/flat-rate` | Create flat-rate pricing |
| GET | `/api/pricing/configurations` | Get all pricing configs |
| GET | `/api/pricing/configurations/{id}` | Get config by ID |
| POST | `/api/pricing/configurations/{id}/activate` | Activate pricing config |
| GET | `/api/pricing/calculate` | Calculate fare |
| DELETE | `/api/pricing/configurations/{id}` | Delete pricing config |

### 👤 Regular User Management

| Method | Endpoint | Purpose |
|--------|----------|---------|
| POST | `/api/users/regular/register` | Register regular user |
| GET | `/api/users/regular/{id}` | Get user details |
| POST | `/api/users/regular/{id}/wallet/add` | Add wallet balance |
| GET | `/api/users/regular/{id}/wallet/balance` | Get wallet balance |
| GET | `/api/users/regular/{id}/loyalty-points` | Get loyalty points |
| POST | `/api/users/regular/{id}/loyalty-points/redeem` | Redeem loyalty points |
| GET | `/api/users/regular/{id}/can-afford` | Check if can afford fare |

### 🏢 Station User Management

| Method | Endpoint | Purpose |
|--------|----------|---------|
| POST | `/api/users/station/register` | Register station user |
| GET | `/api/users/station/{id}` | Get station user details |
| POST | `/api/users/station/{id}/start-duty` | Start duty |
| POST | `/api/users/station/{id}/end-duty` | End duty |
| GET | `/api/users/station/{id}/duty-status` | Check duty status |
| GET | `/api/users/station/on-duty` | Get all users on duty |
| GET | `/api/users/station/department/{name}` | Get users by department |

### 🎫 Ticket Management

| Method | Endpoint | Purpose |
|--------|----------|---------|
| POST | `/api/tickets/book` | Book ticket |
| GET | `/api/tickets` | Get all tickets |
| GET | `/api/tickets/{id}` | Get ticket by ID |
| GET | `/api/tickets/user/{id}` | Get user's tickets |
| POST | `/api/tickets/{id}/cancel` | Cancel ticket |
| POST | `/api/tickets/{id}/use` | Mark ticket as used |
| POST | `/api/tickets/{id}/apply-loyalty` | Apply loyalty discount |
| DELETE | `/api/tickets/{id}` | Delete ticket |

---

## 3. Step-by-Step Complete Example

### Step 1: Create a Route

```bash
curl -X POST http://localhost:8080/api/routes \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Green Line",
    "description": "East-West Corridor"
  }'
```

**Response:**
```json
{
  "id": 1,
  "name": "Green Line",
  "description": "East-West Corridor",
  "stations": []
}
```

### Step 2: Add Stations to Route

```bash
# Station 1: Mysore Road
curl -X POST http://localhost:8080/api/routes/1/stations \
  -H "Content-Type: application/json" \
  -d '{
    "stationName": "Mysore Road",
    "stationCode": "MR",
    "order": 1,
    "distanceToNext": 0
  }'

# Station 2: Silk Board
curl -X POST http://localhost:8080/api/routes/1/stations \
  -H "Content-Type: application/json" \
  -d '{
    "stationName": "Silk Board",
    "stationCode": "SB",
    "order": 2,
    "distanceToNext": 2.8
  }'

# Station 3: Indiranagar
curl -X POST http://localhost:8080/api/routes/1/stations \
  -H "Content-Type: application/json" \
  -d '{
    "stationName": "Indiranagar",
    "stationCode": "IG",
    "order": 3,
    "distanceToNext": 3.5
  }'

# Station 4: Whitefield
curl -X POST http://localhost:8080/api/routes/1/stations \
  -H "Content-Type: application/json" \
  -d '{
    "stationName": "Whitefield",
    "stationCode": "WF",
    "order": 4,
    "distanceToNext": 4.2
  }'
```

### Step 3: Create Pricing Configuration

```bash
# Create distance-based pricing: 5 per km
curl -X POST http://localhost:8080/api/pricing/distance-based \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Standard Pricing",
    "baseRatePerKm": 5.0,
    "minimumFare": 10.0,
    "adminId": 1
  }'
```

**Response:**
```json
{
  "id": 1,
  "name": "Standard Pricing",
  "strategyType": "DISTANCE_BASED",
  "baseRatePerKm": 5.0,
  "minimumFare": 10.0,
  "isActive": false
}
```

### Step 4: Activate Pricing Configuration

```bash
curl -X POST http://localhost:8080/api/pricing/configurations/1/activate \
  -H "Content-Type: application/json"
```

### Step 5: Create a Train

```bash
curl -X POST http://localhost:8080/api/trains \
  -H "Content-Type: application/json" \
  -d '{
    "trainName": "Metro Express 01",
    "routeId": 1,
    "capacity": 600,
    "departureTime": "06:00",
    "arrivalTime": "23:00"
  }'
```

**Response:**
```json
{
  "id": 1,
  "name": "Metro Express 01",
  "route": {
    "id": 1,
    "name": "Green Line"
  },
  "capacity": 600,
  "departureTime": "06:00",
  "arrivalTime": "23:00",
  "status": "Scheduled"
}
```

### Step 6: Register a Regular User

```bash
curl -X POST http://localhost:8080/api/users/regular/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Amit Kumar",
    "email": "amit@example.com",
    "password": "password123",
    "phoneNumber": "9876543210"
  }'
```

**Response:**
```json
{
  "id": 1,
  "user": {
    "id": 1,
    "name": "Amit Kumar",
    "email": "amit@example.com"
  },
  "phoneNumber": "9876543210",
  "walletBalance": 0.0,
  "loyaltyPoints": 0
}
```

### Step 7: Add Wallet Balance

```bash
curl -X POST http://localhost:8080/api/users/regular/1/wallet/add \
  -H "Content-Type: application/json" \
  -d '{"amount": 500.0}'
```

### Step 8: Check Fare Between Stations

```bash
# From Mysore Road (station 1) to Whitefield (station 4)
# Distance: 0 + 2.8 + 3.5 + 4.2 = 10.5 km
# Fare: 10.5 * 5 = 52.5
curl -X GET "http://localhost:8080/api/pricing/calculate?from=1&to=4" \
  -H "Content-Type: application/json"
```

**Response:**
```json
{
  "from_station_id": 1,
  "to_station_id": 4,
  "fare": 52.5
}
```

### Step 9: Check If User Can Afford

```bash
curl -X GET "http://localhost:8080/api/users/regular/1/can-afford?fare=52.5" \
  -H "Content-Type: application/json"
```

**Response:**
```json
{
  "fare": 52.5,
  "balance": 500.0,
  "can_afford": true,
  "shortage": 0
}
```

### Step 10: Book Ticket

```bash
curl -X POST http://localhost:8080/api/tickets/book \
  -H "Content-Type: application/json" \
  -d '{
    "regularUserId": 1,
    "trainId": 1,
    "sourceStationId": 1,
    "destinationStationId": 4,
    "passengerName": "Amit Kumar",
    "travelDate": "2024-12-25"
  }'
```

**Response:**
```json
{
  "id": 1,
  "regularUser": {
    "id": 1,
    "user": {"id": 1, "name": "Amit Kumar"},
    "walletBalance": 447.5,
    "loyaltyPoints": 5
  },
  "passengerName": "Amit Kumar",
  "sourceStation": {"id": 1, "name": "Mysore Road"},
  "destinationStation": {"id": 4, "name": "Whitefield"},
  "train": {"id": 1, "name": "Metro Express 01"},
  "fare": 52.5,
  "discount": 0.0,
  "finalPrice": 52.5,
  "status": "ACTIVE",
  "bookingTime": "2024-04-17T10:30:00",
  "travelDate": "2024-12-25"
}
```

### Step 11: Check Wallet Balance

```bash
curl -X GET http://localhost:8080/api/users/regular/1/wallet/balance \
  -H "Content-Type: application/json"
```

**Response:**
```json
{
  "balance": 447.5
}
```

### Step 12: Get Loyalty Points

```bash
curl -X GET http://localhost:8080/api/users/regular/1/loyalty-points \
  -H "Content-Type: application/json"
```

**Response:**
```json
{
  "loyalty_points": 5
}
```

### Step 13: Register Station Staff

```bash
curl -X POST http://localhost:8080/api/users/station/register \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Ramesh Singh",
    "email": "ramesh@metro.com",
    "password": "password123",
    "stationId": 1,
    "employeeId": "EMP0001",
    "department": "Operations"
  }'
```

### Step 14: Start Duty

```bash
curl -X POST http://localhost:8080/api/users/station/1/start-duty \
  -H "Content-Type: application/json"
```

### Step 15: Check Duty Status

```bash
curl -X GET http://localhost:8080/api/users/station/1/duty-status \
  -H "Content-Type: application/json"
```

**Response:**
```json
{
  "employee_id": "EMP0001",
  "on_duty": true,
  "department": "Operations",
  "station": "Mysore Road"
}
```

---

## 4. Error Handling

### Common Error Codes

| Status | Error | Solution |
|--------|-------|----------|
| 400 | Invalid input | Check request format and data types |
| 404 | Resource not found | Verify ID exists in database |
| 409 | Conflict/duplicate | Check for duplicate entries |
| 500 | Server error | Check application logs |

### Example Error Response

```json
{
  "timestamp": "2024-04-17T10:35:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Wallet balance insufficient. Required: 100.0, Available: 50.0"
}
```

---

## 5. Testing the Application

### Test Workflow

```bash
#!/bin/bash

# Save IDs from responses
ROUTE_ID=1
STATION_1=1
STATION_2=2
TRAIN_ID=1
USER_ID=1
TICKET_ID=1

# Test 1: Get all routes
echo "Test 1: Fetching all routes..."
curl -X GET http://localhost:8080/api/routes

# Test 2: Get all trains
echo "Test 2: Fetching all trains..."
curl -X GET http://localhost:8080/api/trains

# Test 3: Get all tickets
echo "Test 3: Fetching all tickets..."
curl -X GET http://localhost:8080/api/tickets

# Test 4: Cancel ticket and refund
echo "Test 4: Cancelling ticket..."
curl -X POST http://localhost:8080/api/tickets/$TICKET_ID/cancel

# Test 5: Check refunded wallet
echo "Test 5: Checking refunded wallet..."
curl -X GET http://localhost:8080/api/users/regular/$USER_ID/wallet/balance
```

---

## 6. Performance Tips

1. **Add Indices**: Create database indices on frequently queried fields
2. **Pagination**: Implement for large result sets
3. **Caching**: Cache active pricing configuration
4. **Lazy Loading**: Use FetchType.LAZY for heavy relationships
5. **Connection Pooling**: Configure HikariCP

---

## 7. Next Steps

- Add authentication/authorization
- Implement search and filtering
- Add reporting endpoints
- Set up monitoring and logging
- Deploy to production

---

**Happy Booking! 🚇**
