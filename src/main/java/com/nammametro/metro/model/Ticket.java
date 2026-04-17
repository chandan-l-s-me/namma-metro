package com.nammametro.metro.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@Table(name = "TICKET")
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

    @JsonProperty("price")
    private Double fare;

    private String status;

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

    public Double getFare() {
    return fare;
    }

    public void setFare(Double fare) {
    this.fare = fare;
    }

    public String getStatus() {
    return status;
    }

    public void setStatus(String status) {
    this.status = status;
    }
}