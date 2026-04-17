package com.nammametro.metro.dto;

import lombok.Data;

@Data
public class RouteStationRequest {
    private String stationName;
    private String stationCode;
    private Integer order;
    private Double distanceToNext;
}
