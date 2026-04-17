package com.nammametro.metro.repository;

import com.nammametro.metro.model.Station;
import com.nammametro.metro.model.Route;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * StationRepository - Data access for Station entity
 * SOLID: Dependency Inversion - abstract repository pattern
 */
@Repository
public interface StationRepository extends JpaRepository<Station, Long> {

    List<Station> findByRoute(Route route);

    List<Station> findByRouteOrderByOrder(Route route);

    Optional<Station> findByCodeAndRoute(String code, Route route);

    Optional<Station> findByName(String name);
}
