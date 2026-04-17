package com.nammametro.metro.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StationResponse {
    private Long id;
    private String name;
    private String code;
    private Integer order;
    private Double distanceToNext;
    private Double cumulativeDistance;
}
