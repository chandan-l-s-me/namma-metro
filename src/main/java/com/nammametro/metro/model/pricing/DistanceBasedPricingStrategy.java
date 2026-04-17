package com.nammametro.metro.model.pricing;

import com.nammametro.metro.model.Distance;
import com.nammametro.metro.model.Station;

/**
 * Distance-Based Pricing Strategy - Admin-defined base rate per km
 * SOLID: Single Responsibility - only handles distance-based calculation
 * GRASP: Strategy Object
 */
public class DistanceBasedPricingStrategy implements PricingStrategy {

    /**
     * Base fare per kilometer (set by admin)
     */
    private final Double baseRatePerKm;

    /**
     * Minimum fare threshold
     */
    private final Double minimumFare;

    public DistanceBasedPricingStrategy(Double baseRatePerKm, Double minimumFare) {
        if (baseRatePerKm <= 0) {
            throw new IllegalArgumentException("Base rate must be positive");
        }
        this.baseRatePerKm = baseRatePerKm;
        this.minimumFare = minimumFare > 0 ? minimumFare : 0.0;
    }

    public DistanceBasedPricingStrategy(Double baseRatePerKm) {
        this(baseRatePerKm, 10.0); // Default minimum fare
    }

    @Override
    public Double calculateFare(Station fromStation, Station toStation) {
        if (fromStation == null || toStation == null) {
            throw new IllegalArgumentException("Source and destination stations cannot be null");
        }

        if (fromStation.getId().equals(toStation.getId())) {
            throw new IllegalArgumentException("Source and destination cannot be the same");
        }

        Distance distance = Distance.between(fromStation, toStation);
        Double fare = distance.getKilometers() * baseRatePerKm;

        // Apply minimum fare
        return Math.max(fare, minimumFare);
    }

    @Override
    public String getStrategyName() {
        return "Distance-Based Pricing";
    }

    @Override
    public String getDescription() {
        return String.format("Base rate: %.2f per km, Minimum: %.2f", baseRatePerKm, minimumFare);
    }

    public Double getBaseRatePerKm() {
        return baseRatePerKm;
    }

    public Double getMinimumFare() {
        return minimumFare;
    }
}
