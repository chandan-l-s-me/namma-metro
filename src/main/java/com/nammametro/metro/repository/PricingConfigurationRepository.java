package com.nammametro.metro.repository;

import com.nammametro.metro.model.PricingConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * PricingConfigurationRepository - Data access for PricingConfiguration entity
 * SOLID: Dependency Inversion - abstract repository pattern
 */
@Repository
public interface PricingConfigurationRepository extends JpaRepository<PricingConfiguration, Long> {

    Optional<PricingConfiguration> findByNameAndIsActiveTrue(String name);

    List<PricingConfiguration> findByIsActiveTrue();

    Optional<PricingConfiguration> findByStrategyType(String strategyType);
}
