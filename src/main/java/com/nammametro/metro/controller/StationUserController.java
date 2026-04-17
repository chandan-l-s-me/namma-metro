package com.nammametro.metro.controller;

import com.nammametro.metro.model.StationUser;
import com.nammametro.metro.model.User;
import com.nammametro.metro.service.StationUserService;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * StationUserController - API endpoints for station user management
 * Handles registration and duty management for station staff
 */
@RestController
@RequestMapping("/api/users/station")
public class StationUserController {

    private final StationUserService stationUserService;

    public StationUserController(StationUserService stationUserService) {
        this.stationUserService = stationUserService;
    }

    /**
     * Register a new station user
     * POST /api/users/station/register
     */
    @PostMapping("/register")
    public StationUser registerStationUser(@RequestBody Map<String, Object> request) {
        String name = (String) request.get("name");
        String email = (String) request.get("email");
        String password = (String) request.get("password");
        Long stationId = ((Number) request.get("stationId")).longValue();
        String employeeId = (String) request.get("employeeId");
        String department = (String) request.get("department");

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);

        return stationUserService.registerStationUser(user, stationId, employeeId, department);
    }

    /**
     * Get station user by ID
     * GET /api/users/station/{id}
     */
    @GetMapping("/{id}")
    public StationUser getStationUserById(@PathVariable Long id) {
        return stationUserService.getStationUserById(id)
                .orElseThrow(() -> new RuntimeException("Station user not found"));
    }

    /**
     * Start duty
     * POST /api/users/station/{id}/start-duty
     */
    @PostMapping("/{id}/start-duty")
    public StationUser startDuty(@PathVariable Long id) {
        return stationUserService.startDuty(id);
    }

    /**
     * End duty
     * POST /api/users/station/{id}/end-duty
     */
    @PostMapping("/{id}/end-duty")
    public StationUser endDuty(@PathVariable Long id) {
        return stationUserService.endDuty(id);
    }

    /**
     * Check duty status
     * GET /api/users/station/{id}/duty-status
     */
    @GetMapping("/{id}/duty-status")
    public Map<String, Object> getDutyStatus(@PathVariable Long id) {
        StationUser stationUser = stationUserService.getStationUserById(id)
                .orElseThrow(() -> new RuntimeException("Station user not found"));

        return Map.of(
                "employee_id", stationUser.getEmployeeId(),
                "on_duty", stationUser.isOnDuty(),
                "department", stationUser.getDepartment(),
                "station", stationUser.getStation().getName()
        );
    }

    /**
     * Get all station users on duty
     * GET /api/users/station/on-duty
     */
    @GetMapping("/on-duty")
    public List<StationUser> getAllStationUsersOnDuty() {
        return stationUserService.getAllStationUsersOnDuty();
    }

    /**
     * Get station users by department
     * GET /api/users/station/department/{department}
     */
    @GetMapping("/department/{department}")
    public List<StationUser> getStationUsersByDepartment(@PathVariable String department) {
        return stationUserService.getStationUsersByDepartment(department);
    }

    /**
     * Update department
     * PUT /api/users/station/{id}/department
     */
    @PutMapping("/{id}/department")
    public StationUser updateDepartment(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        String newDepartment = request.get("department");
        return stationUserService.updateDepartment(id, newDepartment);
    }

    /**
     * Update shift timing
     * PUT /api/users/station/{id}/shift
     */
    @PutMapping("/{id}/shift")
    public StationUser updateShiftTiming(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        String shiftTiming = request.get("shiftTiming");
        return stationUserService.updateShiftTiming(id, shiftTiming);
    }
}
