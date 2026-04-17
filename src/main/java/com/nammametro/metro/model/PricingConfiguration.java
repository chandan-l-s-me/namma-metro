package com.nammametro.metro.model;

import com.nammametro.metro.model.pricing.PricingStrategy;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * PricingConfiguration Entity - Admin-defined pricing configuration
 * GRASP: Information Expert, Configuration holder
 * SOLID: Single Responsibility - manages pricing configuration
 */
@Entity
@Table(name = "PRICING_CONFIGURATION")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PricingConfiguration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Name of this pricing configuration
     */
    private String name;

    /**
     * Whether this is the active configuration
     */
    private Boolean isActive;

    /**
     * Base rate per kilometer (used by distance-based pricing)
     */
    private Double baseRatePerKm;

    /**
     * Minimum fare to charge
     */
    private Double minimumFare;

    /**
     * Maximum fare cap (if any)
     */
    private Double maximumFare;

    /**
     * Strategy type: DISTANCE_BASED, FLAT_RATE, PEAK_HOUR
     */
    private String strategyType;

    /**
     * Created by (admin user ID)
     */
    private Long createdByAdminId;

    /**
     * Description
     */
    private String description;

    /**
     * Effective date
     */
    private String effectiveDate;

    public PricingConfiguration(String name, Double baseRatePerKm, Double minimumFare) {
        this.name = name;
        this.baseRatePerKm = baseRatePerKm;
        this.minimumFare = minimumFare;
        this.isActive = false;
        this.strategyType = "DISTANCE_BASED";
    }

    /**
     * Activate this configuration
     */
    public void activate() {
        this.isActive = true;
    }

    /**
     * Deactivate this configuration
     */
    public void deactivate() {
        this.isActive = false;
    }

    /**
     * Get display name
     */
    public String getDisplayName() {
        return String.format("%s (Active: %s)", name, isActive ? "Yes" : "No");
    }
}
