package com.nammametro.metro.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nammametro.metro.model.Notification;
import com.nammametro.metro.observer.Observer;
import com.nammametro.metro.repository.NotificationRepository;

@Service
public class NotificationService {

    private List<Observer> observers = new ArrayList<>();

    @Autowired
    private NotificationRepository notificationRepository;

    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    public void notifyPassengers(String message) {
        for (Observer observer : observers) {
            observer.update(message);
        }
    }

    // backward compatibility (your old code)
    public void notifyUsers(String message) {
        notifyPassengers(message);
    }

    // ✅ Create and store notification
    public Notification createNotification(String type, String title, String message, Long userId) {
        Notification notification = new Notification(type, title, message, userId);
        return notificationRepository.save(notification);
    }

    // ✅ Create notification with related ID
    public Notification createNotification(String type, String title, String message, Long userId, String relatedId) {
        Notification notification = new Notification(type, title, message, userId);
        notification.setRelatedId(relatedId);
        return notificationRepository.save(notification);
    }

    // ✅ Get all notifications for a user
    public List<Notification> getUserNotifications(Long userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    // ✅ Get unread notifications for a user
    public List<Notification> getUnreadNotifications(Long userId) {
        return notificationRepository.findByUserIdAndIsReadFalse(userId);
    }

    // ✅ Mark notification as read
    public Notification markAsRead(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId).orElse(null);
        if (notification != null) {
            notification.setRead(true);
            return notificationRepository.save(notification);
        }
        return null;
    }

    // ✅ Mark all notifications as read for a user
    public void markAllAsRead(Long userId) {
        List<Notification> unread = notificationRepository.findByUserIdAndIsReadFalse(userId);
        unread.forEach(n -> n.setRead(true));
        notificationRepository.saveAll(unread);
    }

    // ✅ Delete notification
    public void deleteNotification(Long notificationId) {
        notificationRepository.deleteById(notificationId);
    }
}