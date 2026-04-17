package com.nammametro.metro.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * Route Entity - Represents a metro route with multiple stations
 * GRASP: Entity, Aggregate Root
 * SOLID: Single Responsibility - manages route data
 */
@Entity
@Table(name = "ROUTE")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Route {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    @OneToMany(mappedBy = "route", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Station> stations;

    public Route(String name, String description) {
        this.name = name;
        this.description = description;
    }

    /**
     * Get total distance of the route
     */
    public Double getTotalDistance() {
        if (stations == null || stations.isEmpty()) {
            return 0.0;
        }
        Double total = 0.0;
        for (int i = 0; i < stations.size() - 1; i++) {
            total += stations.get(i).getDistanceToNext();
        }
        return total;
    }

    /**
     * Get station by order
     */
    public Station getStationByOrder(Integer order) {
        if (stations != null) {
            return stations.stream()
                    .filter(s -> s.getOrder().equals(order))
                    .findFirst()
                    .orElse(null);
        }
        return null;
    }

    /**
     * Get number of stations on route
     */
    public Integer getStationCount() {
        return stations != null ? stations.size() : 0;
    }
}
