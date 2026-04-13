package com.nammametro.metro.service;
import com.nammametro.metro.repository.TrainRepository;
import com.nammametro.metro.model.Train;
import org.springframework.stereotype.Service;
import com.nammametro.metro.model.state.*;

import java.util.List;
import java.util.Optional;

@Service
public class TrainService {

    private final TrainRepository trainRepository;

    public TrainService(TrainRepository trainRepository) {
        this.trainRepository = trainRepository;
    }

    // Create Train
    public Train addTrain(Train train) {
        train.setStatus("Scheduled"); // default state
        return trainRepository.save(train);
    }

    // Get All Trains
    public List<Train> getAllTrains() {
        return trainRepository.findAll();
    }

    // Get Train by ID
    public Optional<Train> getTrainById(Long id) {
        return trainRepository.findById(id);
    }

    // Update Train Status
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
                train.setState(new ScheduledState());
        }

        return trainRepository.save(train);
    }

    // Delete Train
    public void deleteTrain(Long id) {
        trainRepository.deleteById(id);
    }
}