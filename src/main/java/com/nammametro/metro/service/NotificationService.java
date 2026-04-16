package com.nammametro.metro.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.nammametro.metro.observer.Observer;

@Service
public class NotificationService {

    private List<Observer> observers = new ArrayList<>();

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
}