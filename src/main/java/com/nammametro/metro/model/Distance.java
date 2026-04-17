package com.nammametro.metro.model;

/**
 * Value Object for Distance
 * SOLID: Single Responsibility - represents and calculates distance
 * GRASP: Value Object - immutable, meaningful distance calculation
 */
public class Distance {

    private final Double kilometers;

    public Distance(Double kilometers) {
        if (kilometers < 0) {
            throw new IllegalArgumentException("Distance cannot be negative");
        }
        this.kilometers = kilometers;
    }

    public Double getKilometers() {
        return kilometers;
    }

    public Double getMeters() {
        return kilometers * 1000;
    }

    /**
     * Calculate distance between two stations
     */
    public static Distance between(Station from, Station to) {
        if (from.getRoute().getId().equals(to.getRoute().getId())) {
            Double diff = Math.abs(to.getCumulativeDistance() - from.getCumulativeDistance());
            return new Distance(diff);
        }
        throw new IllegalArgumentException("Stations must be on the same route");
    }

    @Override
    public String toString() {
        return String.format("%.2f km", kilometers);
    }
}
