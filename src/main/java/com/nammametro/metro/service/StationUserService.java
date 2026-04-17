package com.nammametro.metro.service;

import com.nammametro.metro.model.*;
import com.nammametro.metro.repository.StationUserRepository;
import com.nammametro.metro.repository.StationRepository;
import com.nammametro.metro.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * StationUserService - Business logic for station user management
 * SOLID: Single Responsibility - only manages station users
 * GRASP: Service, Façade
 */
@Service
@Transactional
public class StationUserService {

    private final StationUserRepository stationUserRepository;
    private final StationRepository stationRepository;
    private final UserRepository userRepository;

    public StationUserService(StationUserRepository stationUserRepository,
                             StationRepository stationRepository,
                             UserRepository userRepository) {
        this.stationUserRepository = stationUserRepository;
        this.stationRepository = stationRepository;
        this.userRepository = userRepository;
    }

    /**
     * Register a new station user
     * GRASP: Creator - service creates station users
     */
    public StationUser registerStationUser(User user, Long stationId, String employeeId,
                                          String department) {
        if (user == null || user.getEmail() == null) {
            throw new IllegalArgumentException("User and email cannot be null");
        }

        Station station = stationRepository.findById(stationId)
                .orElseThrow(() -> new RuntimeException("Station not found"));

        // Check if station already has a user assigned
        Optional<StationUser> existingStationUser = stationUserRepository.findByStation(station);
        if (existingStationUser.isPresent()) {
            throw new RuntimeException("Station already has a user assigned");
        }

        // Save base user
        User savedUser = userRepository.save(user);

        // Create station user profile
        StationUser stationUser = new StationUser(savedUser, station, employeeId, department);
        stationUser.setOnDuty(false);

        return stationUserRepository.save(stationUser);
    }

    /**
     * Get station user by ID
     */
    public Optional<StationUser> getStationUserById(Long id) {
        return stationUserRepository.findById(id);
    }

    /**
     * Get station user by user ID
     */
    public Optional<StationUser> getStationUserByUserId(Long userId) {
        return stationUserRepository.findByUserId(userId);
    }

    /**
     * Get station user by station
     */
    public Optional<StationUser> getStationUserByStation(Long stationId) {
        Station station = stationRepository.findById(stationId)
                .orElseThrow(() -> new RuntimeException("Station not found"));
        return stationUserRepository.findByStation(station);
    }

    /**
     * Start duty for station user
     */
    public StationUser startDuty(Long stationUserId) {
        StationUser stationUser = stationUserRepository.findById(stationUserId)
                .orElseThrow(() -> new RuntimeException("Station user not found"));

        stationUser.startDuty();
        return stationUserRepository.save(stationUser);
    }

    /**
     * End duty for station user
     */
    public StationUser endDuty(Long stationUserId) {
        StationUser stationUser = stationUserRepository.findById(stationUserId)
                .orElseThrow(() -> new RuntimeException("Station user not found"));

        stationUser.endDuty();
        return stationUserRepository.save(stationUser);
    }

    /**
     * Check if station user is on duty
     */
    public Boolean isOnDuty(Long stationUserId) {
        StationUser stationUser = stationUserRepository.findById(stationUserId)
                .orElseThrow(() -> new RuntimeException("Station user not found"));

        return stationUser.isOnDuty();
    }

    /**
     * Get all station users on duty
     */
    public List<StationUser> getAllStationUsersOnDuty() {
        return stationUserRepository.findByOnDutyTrue();
    }

    /**
     * Get all station users by department
     */
    public List<StationUser> getStationUsersByDepartment(String department) {
        return stationUserRepository.findByDepartment(department);
    }

    /**
     * Update department
     */
    public StationUser updateDepartment(Long stationUserId, String newDepartment) {
        StationUser stationUser = stationUserRepository.findById(stationUserId)
                .orElseThrow(() -> new RuntimeException("Station user not found"));

        stationUser.setDepartment(newDepartment);
        return stationUserRepository.save(stationUser);
    }

    /**
     * Update shift timing
     */
    public StationUser updateShiftTiming(Long stationUserId, String shiftTiming) {
        StationUser stationUser = stationUserRepository.findById(stationUserId)
                .orElseThrow(() -> new RuntimeException("Station user not found"));

        stationUser.setShiftTiming(shiftTiming);
        return stationUserRepository.save(stationUser);
    }
}
