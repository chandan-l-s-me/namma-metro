package com.nammametro.metro.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TrainResponse {
    private Long id;
    private String trainName;
    private Long routeId;
    private String routeName;
    private Integer capacity;
    private String departureTime;
    private String arrivalTime;
    private String status;
    private String sourceStation;
    private String destinationStation;
}
