package com.nammametro.metro.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class RouteResponse {
    private Long id;
    private String name;
    private String description;
    private Integer stationCount;
    private Double totalDistance;
    private List<StationResponse> stations;
}
