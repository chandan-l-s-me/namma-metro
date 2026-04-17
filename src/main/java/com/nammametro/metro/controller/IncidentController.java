package com.nammametro.metro.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.nammametro.metro.model.Incident;
import com.nammametro.metro.service.IncidentService;
import com.nammametro.metro.service.NotificationService;

@RestController
@RequestMapping("/incidents")
public class IncidentController {

    @Autowired
    private IncidentService incidentService;

    @Autowired
    private NotificationService notificationService;

    // ✅ Create Incident
    @PostMapping
    public Incident reportIncident(@RequestBody Incident incident) {

        Incident saved = incidentService.addIncident(incident);

        notificationService.notifyUsers(
                "Incident reported: " + incident.getDescription() +
                " at " + incident.getLocation()
        );

        return saved;
    }

    // ✅ Get All Incidents
    @GetMapping
    public List<Incident> getAllIncidents() {
        return incidentService.getAllIncidents();
    }

    // ✅ Get Incident by ID (FIXED - THIS WAS MISSING)
    @GetMapping("/{id}")
    public Incident getIncidentById(@PathVariable Long id) {
        return incidentService.getIncidentById(id);
    }

    // ✅ Admin Report
    @GetMapping("/report")
    public String getReport() {
        return "Total incidents: " + incidentService.getAllIncidents().size();
    }
}