package com.nammametro.metro.model.pricing;

import com.nammametro.metro.model.Station;

/**
 * PricingStrategy Interface - Strategy Pattern for different pricing schemes
 * SOLID: Open/Closed Principle - open for extension
 * SOLID: Dependency Inversion - depend on abstraction, not concrete implementations
 * GRASP: Polymorphism
 */
public interface PricingStrategy {

    /**
     * Calculate fare between two stations
     * @param fromStation Source station
     * @param toStation Destination station
     * @return Calculated fare
     */
    Double calculateFare(Station fromStation, Station toStation);

    /**
     * Get strategy name
     */
    String getStrategyName();

    /**
     * Get strategy description
     */
    String getDescription();
}
