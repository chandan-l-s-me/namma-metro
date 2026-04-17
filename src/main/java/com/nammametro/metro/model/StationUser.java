package com.nammametro.metro.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * StationUser Entity - Manages and monitors a specific station
 * GRASP: Role Object, Responsibility assignment
 * SOLID: Liskov Substitution - StationUser is-a User with specific responsibilities
 */
@Entity
@Table(name = "STATION_USER")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class StationUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Primary station managed by this user
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "station_id", nullable = false)
    private Station station;

    /**
     * Employee ID
     */
    private String employeeId;

    /**
     * Department (e.g., Operations, Security, Maintenance)
     */
    private String department;

    /**
     * Shift timing
     */
    private String shiftTiming;

    /**
     * Whether this user is on duty
     */
    private Boolean onDuty;

    public StationUser(User user, Station station, String employeeId, String department) {
        this.user = user;
        this.station = station;
        this.employeeId = employeeId;
        this.department = department;
        this.onDuty = false;
    }

    /**
     * Start duty
     */
    public void startDuty() {
        this.onDuty = true;
    }

    /**
     * End duty
     */
    public void endDuty() {
        this.onDuty = false;
    }

    /**
     * Check if user is on duty
     */
    public Boolean isOnDuty() {
        return onDuty != null && onDuty;
    }

    /**
     * Get user role
     */
    public UserRole getRole() {
        return UserRole.STATION_USER;
    }
}
