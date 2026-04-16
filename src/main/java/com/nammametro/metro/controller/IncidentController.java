package com.nammametro.metro.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    // Report Incident
    @PostMapping
    public Incident reportIncident(@RequestBody Incident incident) {

        Incident saved = incidentService.addIncident(incident);

        notificationService.notifyUsers(
                "Incident reported: " + incident.getDescription() +
                " at " + incident.getLocation()
        );

        return saved;
    }

    // Get All Incidents
    @GetMapping
    public List<Incident> getAllIncidents() {
        return incidentService.getAllIncidents();
    }

    // Admin Report
    @GetMapping("/report")
    public String getReport() {
        return "Total incidents: " + incidentService.getAllIncidents().size();
    }
}