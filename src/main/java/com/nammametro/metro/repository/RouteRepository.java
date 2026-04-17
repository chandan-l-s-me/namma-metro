package com.nammametro.metro.repository;

import com.nammametro.metro.model.Route;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * RouteRepository - Data access for Route entity
 * SOLID: Dependency Inversion - abstract repository pattern
 */
@Repository
public interface RouteRepository extends JpaRepository<Route, Long> {

    Optional<Route> findByName(String name);

    List<Route> findAll();
}
