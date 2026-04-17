package com.nammametro.metro.controller;

import com.nammametro.metro.dto.TrainResponse;
import com.nammametro.metro.model.Train;
import com.nammametro.metro.service.NotificationService;
import com.nammametro.metro.service.TrainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * TrainController - API endpoints for train management
 * Handles train creation, status updates, and inquiry
 */
@RestController
@RequestMapping("/api/trains")
public class TrainController {

    @Autowired
    private NotificationService notificationService;

    private final TrainService trainService;

    public TrainController(TrainService trainService) {
        this.trainService = trainService;
    }

    /**
     * Create a new train
     * POST /api/trains
     * Request body:
     * {
     *   "trainName": "Metro Line 1 - Train 01",
     *   "routeId": 1,
     *   "capacity": 500,
     *   "departureTime": "06:00",
     *   "arrivalTime": "22:00"
     * }
     */
    @PostMapping
    public TrainResponse createTrain(@RequestBody Map<String, Object> request) {
        String trainName = (String) request.get("trainName");
        Long routeId = ((Number) request.get("routeId")).longValue();
        Integer capacity = ((Number) request.get("capacity")).intValue();
        String departureTime = (String) request.get("departureTime");
        String arrivalTime = (String) request.get("arrivalTime");

        Train train = trainService.addTrain(trainName, routeId, capacity, departureTime, arrivalTime);

        // Notify through observer pattern
        notifyUsers("New train " + trainName + " created on route");

        return toTrainResponse(train);
    }

    /**
     * Get all trains
     * GET /api/trains
     */
    @GetMapping
    public List<TrainResponse> getAllTrains() {
        return trainService.getAllTrains().stream()
                .map(this::toTrainResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get train by ID
     * GET /api/trains/{id}
     */
    @GetMapping("/{id}")
    public TrainResponse getTrainById(@PathVariable Long id) {
        Train train = trainService.getTrainById(id)
                .orElseThrow(() -> new RuntimeException("Train not found"));
        return toTrainResponse(train);
    }

    /**
     * Get trains by route
     * GET /api/trains/route/{routeId}
     */
    @GetMapping("/route/{routeId}")
    public List<TrainResponse> getTrainsByRoute(@PathVariable Long routeId) {
        return trainService.getTrainsByRoute(routeId).stream()
                .map(this::toTrainResponse)
                .collect(Collectors.toList());
    }

    /**
     * Update train status
     * PUT /api/trains/{id}/status
     * Request body: { "status": "Running" | "Delayed" | "Cancelled" }
     */
    @PutMapping("/{id}/status")
    public Train updateTrainStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        String status = request.get("status");
        Train train = trainService.updateTrainStatus(id, status);

        // Notify through observer pattern
        notifyUsers("Train status updated to " + status);

        return train;
    }

    /**
     * Get train source station
     * GET /api/trains/{id}/source
     */
    @GetMapping("/{id}/source")
    public Map<String, String> getSourceStation(@PathVariable Long id) {
        Optional<String> source = trainService.getSourceStation(id);
        return Map.of("source_station", source.orElse("Unknown"));
    }

    /**
     * Get train destination station
     * GET /api/trains/{id}/destination
     */
    @GetMapping("/{id}/destination")
    public Map<String, String> getDestinationStation(@PathVariable Long id) {
        Optional<String> destination = trainService.getDestinationStation(id);
        return Map.of("destination_station", destination.orElse("Unknown"));
    }

    /**
     * Delete train
     * DELETE /api/trains/{id}
     */
    @DeleteMapping("/{id}")
    public Map<String, String> deleteTrain(@PathVariable Long id) {
        trainService.deleteTrain(id);
        return Map.of("message", "Train deleted successfully");
    }

    /**
     * Notify users (observer pattern)
     */
    private void notifyUsers(String message) {
        if (notificationService != null) {
            notificationService.notifyUsers(message);
        }
    }

    private TrainResponse toTrainResponse(Train train) {
        return new TrainResponse(
                train.getId(),
                train.getName(),
                train.getRoute() != null ? train.getRoute().getId() : null,
                train.getRoute() != null ? train.getRoute().getName() : null,
                train.getCapacity(),
                train.getDepartureTime(),
                train.getArrivalTime(),
                train.getStatus(),
                trainService.getSourceStation(train.getId()).orElse("Unknown"),
                trainService.getDestinationStation(train.getId()).orElse("Unknown")
        );
    }
}
