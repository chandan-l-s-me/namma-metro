package com.nammametro.metro.service;

import com.nammametro.metro.dto.RouteStationRequest;
import com.nammametro.metro.model.*;
import com.nammametro.metro.model.pricing.PricingStrategy;
import com.nammametro.metro.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * RouteService - Business logic for Route management
 * SOLID: Single Responsibility - only manages routes
 * GRASP: Service, Façade
 */
@Service
@Transactional
public class RouteService {

    private final RouteRepository routeRepository;
    private final StationRepository stationRepository;

    public RouteService(RouteRepository routeRepository, StationRepository stationRepository) {
        this.routeRepository = routeRepository;
        this.stationRepository = stationRepository;
    }

    /**
     * Create a new route
     * GRASP: Creator - service creates routes
     */
    public Route createRoute(String name, String description) {
        return createRoute(name, description, List.of());
    }

    public Route createRoute(String name, String description, List<RouteStationRequest> stationRequests) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Route name cannot be empty");
        }

        Optional<Route> existing = routeRepository.findByName(name);
        if (existing.isPresent()) {
            throw new IllegalArgumentException("Route with name '" + name + "' already exists");
        }

        Route route = new Route(name, description);
        Route savedRoute = routeRepository.save(route);

        if (stationRequests != null && !stationRequests.isEmpty()) {
            for (RouteStationRequest request : stationRequests) {
                validateStationRequest(request);

                Station station = new Station(
                        request.getStationName().trim(),
                        request.getStationCode().trim(),
                        request.getOrder()
                );
                station.setRoute(savedRoute);
                station.setDistanceToNext(request.getDistanceToNext());
                station.setCumulativeDistance(0.0);
                stationRepository.save(station);
            }
            recomputeCumulativeDistances(savedRoute);
        }

        return savedRoute;
    }

    /**
     * Add station to a route
     */
    public Station addStationToRoute(Long routeId, String stationName, String stationCode,
                                     Integer order, Double distanceToNext) {
        Route route = routeRepository.findById(routeId)
                .orElseThrow(() -> new RuntimeException("Route not found with ID: " + routeId));

        validateStationValues(stationName, stationCode, order, distanceToNext);

        Station station = new Station(stationName, stationCode, order);
        station.setRoute(route);
        station.setDistanceToNext(distanceToNext);
        station.setCumulativeDistance(0.0);
        Station savedStation = stationRepository.save(station);
        recomputeCumulativeDistances(route);
        return savedStation;
    }

    /**
     * Get route by ID
     */
    public Optional<Route> getRouteById(Long id) {
        return routeRepository.findById(id);
    }

    /**
     * Get all routes
     */
    public List<Route> getAllRoutes() {
        return routeRepository.findAll();
    }

    /**
     * Get stations on a route
     */
    public List<Station> getStationsOnRoute(Long routeId) {
        Route route = routeRepository.findById(routeId)
                .orElseThrow(() -> new RuntimeException("Route not found with ID: " + routeId));
        return stationRepository.findByRouteOrderByOrder(route);
    }

    /**
     * Get distance between two stations
     */
    public Distance getDistanceBetweenStations(Long fromStationId, Long toStationId) {
        Station fromStation = stationRepository.findById(fromStationId)
                .orElseThrow(() -> new RuntimeException("From station not found"));
        Station toStation = stationRepository.findById(toStationId)
                .orElseThrow(() -> new RuntimeException("To station not found"));

        return Distance.between(fromStation, toStation);
    }

    /**
     * Delete route (if no trains use it)
     */
    public void deleteRoute(Long routeId) {
        Route route = routeRepository.findById(routeId)
                .orElseThrow(() -> new RuntimeException("Route not found with ID: " + routeId));
        routeRepository.delete(route);
    }

    private void validateStationRequest(RouteStationRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Station request cannot be null");
        }
        validateStationValues(
                request.getStationName(),
                request.getStationCode(),
                request.getOrder(),
                request.getDistanceToNext()
        );
    }

    private void validateStationValues(String stationName, String stationCode, Integer order, Double distanceToNext) {
        if (stationName == null || stationName.trim().isEmpty()) {
            throw new IllegalArgumentException("Station name cannot be empty");
        }
        if (stationCode == null || stationCode.trim().isEmpty()) {
            throw new IllegalArgumentException("Station code cannot be empty");
        }
        if (order == null || order <= 0) {
            throw new IllegalArgumentException("Station order must be greater than zero");
        }
        if (distanceToNext == null || distanceToNext < 0) {
            throw new IllegalArgumentException("Distance to next station cannot be negative");
        }
    }

    private void recomputeCumulativeDistances(Route route) {
        List<Station> stations = stationRepository.findByRouteOrderByOrder(route);
        stations.sort(Comparator.comparing(Station::getOrder));

        double runningDistance = 0.0;
        for (int i = 0; i < stations.size(); i++) {
            Station station = stations.get(i);
            station.setCumulativeDistance(runningDistance);
            stationRepository.save(station);

            if (station.getDistanceToNext() != null) {
                runningDistance += station.getDistanceToNext();
            }
        }
    }
}
