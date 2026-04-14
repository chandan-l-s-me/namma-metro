package com.nammametro.metro.service;

import com.nammametro.metro.observer.Observer;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class NotificationService {

    private List<Observer> observers = new ArrayList<>();

    // add user
    public void addObserver(Observer observer) {
        observers.add(observer);
    }

    // remove user
    public void removeObserver(Observer observer) {
        observers.remove(observer);
    }

    // send notification
    public void notifyUsers(String message) {
        System.out.println("Notification: " + message); // 👈 ADD THIS

        for (Observer obs : observers) {
            obs.update(message);
        }
    }
}