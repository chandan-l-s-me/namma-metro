package com.nammametro.metro.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String passengerName;

    private String source;

    private String destination;

    private double fare;

    private Long trainId;

    public String getPassengerName() {
    return passengerName;                 
    }

    public String getSource() {
    return source;
    }

    public String getDestination() {
    return destination;
    }

    public double getFare() {
    return fare;
    }

    public void setFare(double fare) {
    this.fare = fare;
    }
}