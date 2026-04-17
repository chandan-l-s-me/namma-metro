package com.nammametro.metro.dto;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class RouteCreateRequest {
    private String name;
    private String description;
    private List<RouteStationRequest> stations = new ArrayList<>();
}
