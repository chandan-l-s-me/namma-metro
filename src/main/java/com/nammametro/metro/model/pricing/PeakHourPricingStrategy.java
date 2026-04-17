package com.nammametro.metro.model.pricing;

import com.nammametro.metro.model.Distance;
import com.nammametro.metro.model.Station;
import java.time.LocalDateTime;
import java.time.LocalTime;

/**
 * Peak Hour Pricing Strategy - Variable pricing based on time of day
 * SOLID: Single Responsibility - only handles time-based pricing surcharge
 * GRASP: Strategy Object
 */
public class PeakHourPricingStrategy implements PricingStrategy {

    private final Double baseRatePerKm;
    private final Double minimumFare;
    private final Double peakHourMultiplier; // e.g., 1.5 for 50% surcharge
    private final LocalTime peakStartTime;
    private final LocalTime peakEndTime;

    public PeakHourPricingStrategy(Double baseRatePerKm, Double minimumFare,
                                   Double peakHourMultiplier,
                                   LocalTime peakStartTime, LocalTime peakEndTime) {
        if (baseRatePerKm <= 0 || peakHourMultiplier <= 0) {
            throw new IllegalArgumentException("Rates must be positive");
        }
        this.baseRatePerKm = baseRatePerKm;
        this.minimumFare = minimumFare > 0 ? minimumFare : 0.0;
        this.peakHourMultiplier = peakHourMultiplier;
        this.peakStartTime = peakStartTime;
        this.peakEndTime = peakEndTime;
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
        Double baseFare = distance.getKilometers() * baseRatePerKm;

        // Check if current time is peak hour
        LocalTime currentTime = LocalDateTime.now().toLocalTime();
        if (isPeakHour(currentTime)) {
            baseFare *= peakHourMultiplier;
        }

        return Math.max(baseFare, minimumFare);
    }

    private boolean isPeakHour(LocalTime time) {
        if (peakStartTime.isBefore(peakEndTime)) {
            // Peak hours don't cross midnight
            return !time.isBefore(peakStartTime) && time.isBefore(peakEndTime);
        } else {
            // Peak hours cross midnight
            return !time.isBefore(peakStartTime) || time.isBefore(peakEndTime);
        }
    }

    @Override
    public String getStrategyName() {
        return "Peak Hour Pricing";
    }

    @Override
    public String getDescription() {
        return String.format("Base rate: %.2f/km, Peak multiplier: %.2f, Peak hours: %s-%s",
                baseRatePerKm, peakHourMultiplier, peakStartTime, peakEndTime);
    }

    public Double getBaseRatePerKm() {
        return baseRatePerKm;
    }

    public Double getPeakHourMultiplier() {
        return peakHourMultiplier;
    }
}
