package com.nammametro.metro.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;

/**
 * Ticket Entity - Represents a booked ticket
 * SOLID: Single Responsibility - manages ticket data
 * Updated to reference Station and RegularUser entities
 */
@Entity
@Table(name = "TICKET")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Regular user who booked this ticket
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "regular_user_id", nullable = false)
    private RegularUser regularUser;

    /**
     * Passenger name (for the ticket)
     */
    private String passengerName;

    /**
     * Source station
     * Changed from string to Station entity
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "source_station_id", nullable = false)
    private Station sourceStation;

    /**
     * Destination station
     * Changed from string to Station entity
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "destination_station_id", nullable = false)
    private Station destinationStation;

    /**
     * Train for this ticket
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "train_id", nullable = false)
    private Train train;

    @JsonProperty("price")
    private Double fare;

    /**
     * Discount applied (if any)
     */
    private Double discount;

    /**
     * Final price after discount
     */
    private Double finalPrice;

    private String status; // ACTIVE, CANCELLED, USED, EXPIRED

    /**
     * Booking timestamp
     */
    private LocalDateTime bookingTime;

    /**
     * Travel date
     */
    private String travelDate;

    public Ticket(RegularUser regularUser, String passengerName, Station sourceStation,
                  Station destinationStation, Train train, Double fare) {
        this.regularUser = regularUser;
        this.passengerName = passengerName;
        this.sourceStation = sourceStation;
        this.destinationStation = destinationStation;
        this.train = train;
        this.fare = fare;
        this.discount = 0.0;
        this.finalPrice = fare;
        this.status = "ACTIVE";
        this.bookingTime = LocalDateTime.now();
    }

    /**
     * Apply discount to ticket
     */
    public void applyDiscount(Double discountAmount) {
        if (discountAmount < 0 || discountAmount > fare) {
            throw new IllegalArgumentException("Invalid discount amount");
        }
        this.discount = discountAmount;
        this.finalPrice = fare - discountAmount;
    }

    /**
     * Cancel ticket
     */
    public void cancel() {
        if ("USED".equals(status) || "EXPIRED".equals(status)) {
            throw new RuntimeException("Cannot cancel " + status + " ticket");
        }
        this.status = "CANCELLED";
    }

    /**
     * Mark ticket as used
     */
    public void markAsUsed() {
        this.status = "USED";
    }

    /**
     * Get source station name
     */
    public String getSourceStationName() {
        return sourceStation != null ? sourceStation.getName() : "";
    }

    /**
     * Get destination station name
     */
    public String getDestinationStationName() {
        return destinationStation != null ? destinationStation.getName() : "";
    }

    /**
     * Get distance traveled
     */
    public Double getDistance() {
        if (sourceStation != null && destinationStation != null) {
            return Distance.between(sourceStation, destinationStation).getKilometers();
        }
        return 0.0;
    }
}