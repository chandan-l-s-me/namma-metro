package com.nammametro.metro.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.nammametro.metro.model.Notification;
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

    // ✅ Get all notifications for a user
    @GetMapping("/user/{userId}")
    public List<Notification> getUserNotifications(@PathVariable Long userId) {
        return notificationService.getUserNotifications(userId);
    }

    // ✅ Get unread notifications for a user
    @GetMapping("/user/{userId}/unread")
    public List<Notification> getUnreadNotifications(@PathVariable Long userId) {
        return notificationService.getUnreadNotifications(userId);
    }

    // ✅ Mark notification as read
    @PutMapping("/{notificationId}/read")
    public Notification markAsRead(@PathVariable Long notificationId) {
        return notificationService.markAsRead(notificationId);
    }

    // ✅ Mark all notifications as read
    @PutMapping("/user/{userId}/read-all")
    public String markAllAsRead(@PathVariable Long userId) {
        notificationService.markAllAsRead(userId);
        return "All notifications marked as read";
    }

    // ✅ Delete notification
    @DeleteMapping("/{notificationId}")
    public String deleteNotification(@PathVariable Long notificationId) {
        notificationService.deleteNotification(notificationId);
        return "Notification deleted";
    }
}