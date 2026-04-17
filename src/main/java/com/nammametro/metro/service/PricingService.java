package com.nammametro.metro.service;

import com.nammametro.metro.model.*;
import com.nammametro.metro.model.pricing.DistanceBasedPricingStrategy;
import com.nammametro.metro.model.pricing.FlatRatePricingStrategy;
import com.nammametro.metro.model.pricing.PeakHourPricingStrategy;
import com.nammametro.metro.model.pricing.PricingStrategy;
import com.nammametro.metro.repository.PricingConfigurationRepository;
import com.nammametro.metro.repository.StationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

/**
 * PricingService - Business logic for pricing calculations
 * SOLID: Single Responsibility - only manages pricing
 * SOLID: Dependency Inversion - depends on PricingStrategy interface
 * GRASP: Service, Façade, Strategy Factory
 */
@Service
@Transactional
public class PricingService {

    private final PricingConfigurationRepository pricingConfigRepository;
    private final StationRepository stationRepository;

    public PricingService(PricingConfigurationRepository pricingConfigRepository,
                         StationRepository stationRepository) {
        this.pricingConfigRepository = pricingConfigRepository;
        this.stationRepository = stationRepository;
    }

    /**
     * Create distance-based pricing configuration (admin action)
     * GRASP: Creator - service creates pricing configurations
     */
    public PricingConfiguration createDistanceBasedPricing(String name, Double baseRatePerKm,
                                                          Double minimumFare, Long adminId) {
        PricingConfiguration config = new PricingConfiguration(name, baseRatePerKm, minimumFare);
        config.setStrategyType("DISTANCE_BASED");
        config.setCreatedByAdminId(adminId);
        return pricingConfigRepository.save(config);
    }

    /**
     * Create flat rate pricing configuration (admin action)
     */
    public PricingConfiguration createFlatRatePricing(String name, Double flatRate, Long adminId) {
        PricingConfiguration config = new PricingConfiguration();
        config.setName(name);
        config.setBaseRatePerKm(flatRate); // Reuse field for flat rate
        config.setStrategyType("FLAT_RATE");
        config.setCreatedByAdminId(adminId);
        config.setIsActive(false);
        return pricingConfigRepository.save(config);
    }

    /**
     * Activate pricing configuration (admin action)
     */
    public PricingConfiguration activatePricingConfiguration(Long configId) {
        // Deactivate all other configurations
        List<PricingConfiguration> activeConfigs = pricingConfigRepository.findByIsActiveTrue();
        for (PricingConfiguration config : activeConfigs) {
            config.deactivate();
            pricingConfigRepository.save(config);
        }

        // Activate the specified configuration
        PricingConfiguration config = pricingConfigRepository.findById(configId)
                .orElseThrow(() -> new RuntimeException("Pricing configuration not found"));
        config.activate();
        return pricingConfigRepository.save(config);
    }

    /**
     * Calculate fare between two stations using active pricing strategy
     * GRASP: Information Expert - knows how to calculate fares
     */
    public Double calculateFare(Long fromStationId, Long toStationId) {
        Station fromStation = stationRepository.findById(fromStationId)
                .orElseThrow(() -> new RuntimeException("From station not found"));
        Station toStation = stationRepository.findById(toStationId)
                .orElseThrow(() -> new RuntimeException("To station not found"));

        PricingStrategy strategy = getActivePricingStrategy();
        return strategy.calculateFare(fromStation, toStation);
    }

    /**
     * Calculate fare with specific pricing configuration
     */
    public Double calculateFareWithConfig(Long fromStationId, Long toStationId, Long configId) {
        Station fromStation = stationRepository.findById(fromStationId)
                .orElseThrow(() -> new RuntimeException("From station not found"));
        Station toStation = stationRepository.findById(toStationId)
                .orElseThrow(() -> new RuntimeException("To station not found"));

        PricingConfiguration config = pricingConfigRepository.findById(configId)
                .orElseThrow(() -> new RuntimeException("Pricing configuration not found"));

        PricingStrategy strategy = createPricingStrategy(config);
        return strategy.calculateFare(fromStation, toStation);
    }

    /**
     * Get active pricing strategy
     * GRASP: Strategy Factory
     */
    private PricingStrategy getActivePricingStrategy() {
        List<PricingConfiguration> activeConfigs = pricingConfigRepository.findByIsActiveTrue();
        if (activeConfigs.isEmpty()) {
            // Default strategy: distance-based with 5 per km, minimum 10
            return new DistanceBasedPricingStrategy(5.0, 10.0);
        }

        PricingConfiguration config = activeConfigs.get(0);
        return createPricingStrategy(config);
    }

    /**
     * Create pricing strategy from configuration
     * GRASP: Strategy Factory Pattern
     * SOLID: Dependency Inversion - returns abstraction
     */
    private PricingStrategy createPricingStrategy(PricingConfiguration config) {
        switch (config.getStrategyType()) {
            case "DISTANCE_BASED":
                return new DistanceBasedPricingStrategy(config.getBaseRatePerKm(),
                        config.getMinimumFare() != null ? config.getMinimumFare() : 0.0);
            case "FLAT_RATE":
                return new FlatRatePricingStrategy(config.getBaseRatePerKm());
            case "PEAK_HOUR":
                return new PeakHourPricingStrategy(config.getBaseRatePerKm(),
                        config.getMinimumFare() != null ? config.getMinimumFare() : 0.0,
                        1.5, // Peak multiplier
                        LocalTime.of(7, 0),   // Peak start
                        LocalTime.of(10, 0)  // Peak end
                );
            default:
                throw new RuntimeException("Unknown pricing strategy: " + config.getStrategyType());
        }
    }

    /**
     * Get all pricing configurations
     */
    public List<PricingConfiguration> getAllPricingConfigurations() {
        return pricingConfigRepository.findAll();
    }

    /**
     * Get pricing configuration by ID
     */
    public Optional<PricingConfiguration> getPricingConfiguration(Long id) {
        return pricingConfigRepository.findById(id);
    }

    /**
     * Delete pricing configuration
     */
    public void deletePricingConfiguration(Long id) {
        PricingConfiguration config = pricingConfigRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Pricing configuration not found"));

        if (config.getIsActive()) {
            throw new RuntimeException("Cannot delete active pricing configuration");
        }

        pricingConfigRepository.delete(config);
    }
}
