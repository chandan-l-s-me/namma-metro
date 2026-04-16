package com.nammametro.metro.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.nammametro.metro.model.Passenger;
import com.nammametro.metro.service.NotificationService;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @PostMapping("/add")
    public String addPassenger(@RequestParam String name) {
        Passenger passenger = new Passenger(name);
        notificationService.addObserver(passenger);
        return "Passenger added: " + name;
    }

    @PostMapping("/send")
    public String sendNotification(@RequestParam String message) {
        notificationService.notifyPassengers(message);
        return "Notification sent: " + message;
    }
}