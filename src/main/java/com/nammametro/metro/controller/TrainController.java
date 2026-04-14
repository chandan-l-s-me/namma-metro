package com.nammametro.metro.controller;

import com.nammametro.metro.model.Train;
import com.nammametro.metro.service.NotificationService;
import com.nammametro.metro.service.TrainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/trains")
public class TrainController {

    @Autowired
    private NotificationService notificationService;

    private final TrainService trainService;

    public TrainController(TrainService trainService) {
        this.trainService = trainService;
    }

    // Add Train
    @PostMapping
    public Train createTrain(@RequestBody Train train) {
        return trainService.addTrain(train);
    }

    // Get All Trains
    @GetMapping
    public List<Train> getAllTrains() {
        return trainService.getAllTrains();
    }

    // Get Train by ID
    @GetMapping("/{id}")
    public Train getTrain(@PathVariable Long id) {
        return trainService.getTrainById(id)
                .orElseThrow(() -> new RuntimeException("Train not found"));
    }

    // Update Status
    @PutMapping("/{id}/status")
    public Train updateStatus(@PathVariable Long id, @RequestParam String status) {
        return trainService.updateTrainStatus(id, status);
    }

    // Delete Train
    @DeleteMapping("/{id}")
    public String deleteTrain(@PathVariable Long id) {
        trainService.deleteTrain(id);
        return "Train deleted successfully";
    }

    @PostMapping("/delay")
    public String delayTrain(@RequestParam String trainName) {

        notificationService.notifyUsers("Train " + trainName + " is delayed!");

        return "Delay notification sent!";
    }

    @PostMapping("/updateStatus")
    public String updateStatus(@RequestParam String trainName, @RequestParam String status) {

        notificationService.notifyUsers(
                "Train " + trainName + " status updated to " + status
        );

        return "Status updated!";
    }
}