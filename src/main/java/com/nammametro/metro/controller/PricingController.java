package com.nammametro.metro.controller;

import com.nammametro.metro.model.PricingConfiguration;
import com.nammametro.metro.service.PricingService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * PricingController - API endpoints for pricing management
 * Admin-only endpoints for configuring ticket pricing strategies
 */
@RestController
@RequestMapping("/api/pricing")
public class PricingController {

    private final PricingService pricingService;

    public PricingController(PricingService pricingService) {
        this.pricingService = pricingService;
    }

    /**
     * Create distance-based pricing configuration
     * POST /api/pricing/distance-based
     */
    @PostMapping("/distance-based")
    public PricingConfiguration createDistanceBasedPricing(
            @RequestBody Map<String, Object> request) {
        String name = (String) request.get("name");
        Double baseRatePerKm = ((Number) request.get("baseRatePerKm")).doubleValue();
        Double minimumFare = ((Number) request.get("minimumFare")).doubleValue();
        Long adminId = ((Number) request.get("adminId")).longValue();

        return pricingService.createDistanceBasedPricing(name, baseRatePerKm, minimumFare, adminId);
    }

    /**
     * Create flat rate pricing configuration
     * POST /api/pricing/flat-rate
     */
    @PostMapping("/flat-rate")
    public PricingConfiguration createFlatRatePricing(
            @RequestBody Map<String, Object> request) {
        String name = (String) request.get("name");
        Double flatRate = ((Number) request.get("flatRate")).doubleValue();
        Long adminId = ((Number) request.get("adminId")).longValue();

        return pricingService.createFlatRatePricing(name, flatRate, adminId);
    }

    /**
     * Get all pricing configurations
     * GET /api/pricing/configurations
     */
    @GetMapping("/configurations")
    public List<PricingConfiguration> getAllPricingConfigurations() {
        return pricingService.getAllPricingConfigurations();
    }

    /**
     * Get pricing configuration by ID
     * GET /api/pricing/configurations/{id}
     */
    @GetMapping("/configurations/{id}")
    public PricingConfiguration getPricingConfiguration(@PathVariable Long id) {
        return pricingService.getPricingConfiguration(id)
                .orElseThrow(() -> new RuntimeException("Pricing configuration not found"));
    }

    /**
     * Activate pricing configuration
     * POST /api/pricing/configurations/{id}/activate
     */
    @PostMapping("/configurations/{id}/activate")
    public PricingConfiguration activatePricingConfiguration(@PathVariable Long id) {
        return pricingService.activatePricingConfiguration(id);
    }

    /**
     * Calculate fare between two stations
     * GET /api/pricing/calculate?from={fromStationId}&to={toStationId}
     */
    @GetMapping("/calculate")
    public Map<String, Object> calculateFare(
            @RequestParam Long from,
            @RequestParam Long to) {
        Double fare = pricingService.calculateFare(from, to);
        return Map.of(
                "from_station_id", from,
                "to_station_id", to,
                "fare", fare
        );
    }

    /**
     * Delete pricing configuration
     * DELETE /api/pricing/configurations/{id}
     */
    @DeleteMapping("/configurations/{id}")
    public Map<String, String> deletePricingConfiguration(@PathVariable Long id) {
        pricingService.deletePricingConfiguration(id);
        return Map.of("message", "Pricing configuration deleted successfully");
    }
}
