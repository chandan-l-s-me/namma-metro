package com.nammametro.metro.controller;

import com.nammametro.metro.dto.RouteCreateRequest;
import com.nammametro.metro.dto.RouteResponse;
import com.nammametro.metro.dto.RouteStationRequest;
import com.nammametro.metro.dto.StationResponse;
import com.nammametro.metro.model.Route;
import com.nammametro.metro.model.Station;
import com.nammametro.metro.model.Distance;
import com.nammametro.metro.service.RouteService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * RouteController - API endpoints for route management
 * REST endpoints for creating and managing metro routes and stations
 */
@RestController
@RequestMapping("/api/routes")
public class RouteController {

    private final RouteService routeService;

    public RouteController(RouteService routeService) {
        this.routeService = routeService;
    }

    /**
     * Create a new route
     * POST /api/routes
     */
    @PostMapping
    public RouteResponse createRoute(@RequestBody RouteCreateRequest request) {
        Route route = routeService.createRoute(
                request.getName(),
                request.getDescription(),
                request.getStations()
        );
        return toRouteResponse(route);
    }

    /**
     * Get all routes
     * GET /api/routes
     */
    @GetMapping
    public List<RouteResponse> getAllRoutes() {
        return routeService.getAllRoutes().stream()
                .map(this::toRouteResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get route by ID
     * GET /api/routes/{id}
     */
    @GetMapping("/{id}")
    public RouteResponse getRouteById(@PathVariable Long id) {
        Route route = routeService.getRouteById(id)
                .orElseThrow(() -> new RuntimeException("Route not found"));
        return toRouteResponse(route);
    }

    /**
     * Add station to route
     * POST /api/routes/{routeId}/stations
     */
    @PostMapping("/{routeId}/stations")
    public StationResponse addStationToRoute(
            @PathVariable Long routeId,
            @RequestBody RouteStationRequest request) {
        String stationName = request.getStationName();
        String stationCode = request.getStationCode();
        Integer order = request.getOrder();
        Double distanceToNext = request.getDistanceToNext();

        Station station = routeService.addStationToRoute(routeId, stationName, stationCode, order, distanceToNext);
        return toStationResponse(station);
    }

    /**
     * Get stations on a route
     * GET /api/routes/{routeId}/stations
     */
    @GetMapping("/{routeId}/stations")
    public List<StationResponse> getStationsOnRoute(@PathVariable Long routeId) {
        return routeService.getStationsOnRoute(routeId).stream()
                .map(this::toStationResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get distance between two stations
     * GET /api/routes/distance?from={fromStationId}&to={toStationId}
     */
    @GetMapping("/distance")
    public Map<String, Object> getDistance(
            @RequestParam Long from,
            @RequestParam Long to) {
        Distance distance = routeService.getDistanceBetweenStations(from, to);
        return Map.of(
                "distance_km", distance.getKilometers(),
                "distance_meters", distance.getMeters(),
                "display", distance.toString()
        );
    }

    /**
     * Delete route
     * DELETE /api/routes/{id}
     */
    @DeleteMapping("/{id}")
    public Map<String, String> deleteRoute(@PathVariable Long id) {
        routeService.deleteRoute(id);
        return Map.of("message", "Route deleted successfully");
    }

    private RouteResponse toRouteResponse(Route route) {
        List<StationResponse> stations = routeService.getStationsOnRoute(route.getId()).stream()
                .map(this::toStationResponse)
                .collect(Collectors.toList());

        double totalDistance = stations.stream()
                .map(StationResponse::getDistanceToNext)
                .filter(distance -> distance != null)
                .reduce(0.0, Double::sum);

        return new RouteResponse(
                route.getId(),
                route.getName(),
                route.getDescription(),
                stations.size(),
                totalDistance,
                stations
        );
    }

    private StationResponse toStationResponse(Station station) {
        return new StationResponse(
                station.getId(),
                station.getName(),
                station.getCode(),
                station.getOrder(),
                station.getDistanceToNext(),
                station.getCumulativeDistance()
        );
    }
}
