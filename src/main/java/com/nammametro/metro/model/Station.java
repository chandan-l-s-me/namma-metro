package com.nammametro.metro.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Station Entity - Represents a metro station on a route
 * GRASP: Entity, Information Expert for station data
 * SOLID: Single Responsibility - manages station data
 */
@Entity
@Table(name = "STATION")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Station {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String code; // Station code (e.g., MG, AS, KR)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_id", nullable = false)
    private Route route;

    /**
     * Order of station in the route (1, 2, 3, ...)
     * Used to calculate distance between stations
     */
    @Column(name = "station_order")
    private Integer order;

    /**
     * Distance to the next station in kilometers
     */
    private Double distanceToNext;

    /**
     * Cumulative distance from starting station
     */
    private Double cumulativeDistance;

    /**
     * Station user who manages this station
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "station_user_id")
    private StationUser stationUser;

    public Station(String name, String code, Integer order) {
        this.name = name;
        this.code = code;
        this.order = order;
    }

    /**
     * Update cumulative distance
     */
    public void updateCumulativeDistance(Double previousCumulativeDistance) {
        if (this.distanceToNext != null && previousCumulativeDistance != null) {
            this.cumulativeDistance = previousCumulativeDistance + this.distanceToNext;
        } else {
            this.cumulativeDistance = 0.0;
        }
    }

    /**
     * Get display name with code
     */
    public String getDisplayName() {
        return String.format("%s (%s)", this.name, this.code);
    }
}
