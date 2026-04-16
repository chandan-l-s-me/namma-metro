package com.nammametro.metro.model;
import com.nammametro.metro.observer.Observer;

public class Passenger implements Observer {

    private String name;

    public Passenger(String name) {
        this.name = name;
    }

    @Override
    public void update(String message) {
        System.out.println("Notification to " + name + ": " + message);
    }
}