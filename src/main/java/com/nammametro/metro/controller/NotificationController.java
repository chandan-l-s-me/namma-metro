package com.nammametro.metro.controller;

import com.nammametro.metro.observer.UserObserver;
import com.nammametro.metro.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notify")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    // Add user to notification list
    @PostMapping("/subscribe")
    public String subscribe(@RequestParam String name) {
        UserObserver user = new UserObserver(name);
        notificationService.addObserver(user);
        return "User subscribed!";
    }

    // Trigger notification
    @PostMapping("/send")
    public String sendNotification(@RequestParam String message) {
        notificationService.notifyUsers(message);
        return "Notification sent!";
    }
}