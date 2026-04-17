package com.nammametro.metro.model.pricing;

import com.nammametro.metro.model.Station;

/**
 * Flat Rate Pricing Strategy - Simple fixed price regardless of distance
 * SOLID: Single Responsibility - only handles flat rate calculation
 * GRASP: Strategy Object
 */
public class FlatRatePricingStrategy implements PricingStrategy {

    private final Double flatRate;

    public FlatRatePricingStrategy(Double flatRate) {
        if (flatRate <= 0) {
            throw new IllegalArgumentException("Flat rate must be positive");
        }
        this.flatRate = flatRate;
    }

    @Override
    public Double calculateFare(Station fromStation, Station toStation) {
        if (fromStation == null || toStation == null) {
            throw new IllegalArgumentException("Source and destination stations cannot be null");
        }

        if (fromStation.getId().equals(toStation.getId())) {
            throw new IllegalArgumentException("Source and destination cannot be the same");
        }

        return flatRate;
    }

    @Override
    public String getStrategyName() {
        return "Flat Rate Pricing";
    }

    @Override
    public String getDescription() {
        return String.format("Fixed fare: %.2f regardless of distance", flatRate);
    }

    public Double getFlatRate() {
        return flatRate;
    }
}
