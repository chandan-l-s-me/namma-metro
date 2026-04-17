package com.nammametro.metro.model;

import com.nammametro.metro.model.state.TrainState;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Train Entity - Represents a metro train
 * SOLID: Single Responsibility - manages train data
 * Updated to reference Route instead of using string fields
 */
@Entity
@Table(name = "TRAIN")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Train {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonProperty("trainName")
    private String name;

    /**
     * Route operated by this train
     * Changed from string to Route entity
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_id")
    private Route route;

    private Integer capacity;

    /**
     * Departure time for this train
     */
    private String departureTime;

    /**
     * Arrival time for this train
     */
    private String arrivalTime;

    private String status;

    @Transient
    private TrainState state;

    public void setState(TrainState state) {
        this.state = state;
        state.handle(this);
    }

    public void setStatus(String status) {
    this.status = status;
    }

    /**
     * Get source station (first station on route)
     */
    public Station getSourceStation() {
        if (route != null && route.getStationCount() > 0) {
            return route.getStationByOrder(1);
        }
        return null;
    }

    /**
     * Get destination station (last station on route)
     */
    public Station getDestinationStation() {
        if (route != null && route.getStationCount() > 0) {
            return route.getStationByOrder(route.getStationCount());
        }
        return null;
    }

    /**
     * Get display name with route
     */
    public String getDisplayName() {
        if (route != null) {
            return String.format("%s (%s)", name, route.getName());
        }
        return name;
    }
}