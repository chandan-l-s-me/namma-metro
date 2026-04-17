package com.nammametro.metro.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nammametro.metro.model.Incident;
import com.nammametro.metro.repository.IncidentRepository;

@Service
public class IncidentService {

    @Autowired
    private IncidentRepository incidentRepository;

    public Incident addIncident(Incident incident) {

        //  Use Factory instead of direct object save
        Incident newIncident = IncidentFactory.createIncident(
                "DEFAULT",   // you can later pass type from request
                incident.getDescription(),
                incident.getLocation()
        );

        return incidentRepository.save(newIncident);
    }

    public List<Incident> getAllIncidents() {
        return incidentRepository.findAll();
    }

    // REQUIRED for /incidents/{id}
    public Incident getIncidentById(Long id) {
        return incidentRepository.findById(id).orElse(null);
    }
}