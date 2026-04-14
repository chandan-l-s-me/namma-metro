package com.nammametro.metro.observer;

public class UserObserver implements Observer {

    private String name;

    public UserObserver(String name) {
        this.name = name;
    }

    @Override
    public void update(String message) {
        System.out.println("Notification for " + name + ": " + message);
    }
}