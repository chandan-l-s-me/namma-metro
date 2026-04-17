package com.nammametro.metro.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TicketResponse {
    private Long id;
    private Long regularUserId;
    private String passengerName;
    private Long trainId;
    private String trainName;
    private String sourceStationName;
    private String destinationStationName;
    private Double fare;
    private Double discount;
    private Double finalPrice;
    private String status;
    private String travelDate;
    private Double distance;
}
