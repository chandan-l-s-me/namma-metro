package com.nammametro.metro.service;
import com.nammametro.metro.repository.TrainRepository;
import com.nammametro.metro.repository.RouteRepository;
import com.nammametro.metro.model.Train;
import com.nammametro.metro.model.Route;
import org.springframework.stereotype.Service;
import com.nammametro.metro.model.state.*;

import java.util.List;
import java.util.Optional;

/**
 * TrainService - Business logic for train management
 * SOLID: Single Responsibility - only manages trains
 * GRASP: Service, Façade
 */
@Service
public class TrainService {

    private final TrainRepository trainRepository;
    private final RouteRepository routeRepository;

    public TrainService(TrainRepository trainRepository, RouteRepository routeRepository) {
        this.trainRepository = trainRepository;
        this.routeRepository = routeRepository;
    }

    /**
     * Create a new train
     * GRASP: Creator - service creates trains
     */
    public Train addTrain(String trainName, Long routeId, Integer capacity,
                         String departureTime, String arrivalTime) {
        if (trainName == null || trainName.trim().isEmpty()) {
            throw new IllegalArgumentException("Train name cannot be empty");
        }

        Route route = routeRepository.findById(routeId)
                .orElseThrow(() -> new RuntimeException("Route not found with ID: " + routeId));

        Train train = new Train();
        train.setName(trainName);
        train.setRoute(route);
        train.setCapacity(capacity);
        train.setDepartureTime(departureTime);
        train.setArrivalTime(arrivalTime);
        train.setStatus("Scheduled");

        return trainRepository.save(train);
    }

    /**
     * Get all trains
     */
    public List<Train> getAllTrains() {
        return trainRepository.findAll();
    }

    /**
     * Get trains by route
     */
    public List<Train> getTrainsByRoute(Long routeId) {
        Route route = routeRepository.findById(routeId)
                .orElseThrow(() -> new RuntimeException("Route not found"));
        return trainRepository.findByRoute(route);
    }

    /**
     * Get train by ID
     */
    public Optional<Train> getTrainById(Long id) {
        return trainRepository.findById(id);
    }

    /**
     * Update train status using State pattern
     */
    public Train updateTrainStatus(Long id, String status) {
        Train train = trainRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Train not found"));

        switch (status) {
            case "Running":
                train.setState(new RunningState());
                break;
            case "Delayed":
                train.setState(new DelayedState());
                break;
            case "Cancelled":
                train.setState(new CancelledState());
                break;
            default:
                throw new IllegalArgumentException("Invalid status: " + status);
        }

        return trainRepository.save(train);
    }

    /**
     * Delete train
     */
    public void deleteTrain(Long id) {
        Train train = trainRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Train not found"));
        trainRepository.delete(train);
    }

    /**
     * Get train source station
     */
    public Optional<String> getSourceStation(Long trainId) {
        return trainRepository.findById(trainId)
                .map(Train::getSourceStation)
                .map(station -> station != null ? station.getName() : null);
    }

    /**
     * Get train destination station
     */
    public Optional<String> getDestinationStation(Long trainId) {
        return trainRepository.findById(trainId)
                .map(Train::getDestinationStation)
                .map(station -> station != null ? station.getName() : null);
    }
}