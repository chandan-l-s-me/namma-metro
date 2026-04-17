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
        return incidentRepository.save(incident);
    }

    public List<Incident> getAllIncidents() {
        return incidentRepository.findAll();
    }

    public Incident getIncidentById(Long id) {
        return incidentRepository.findById(id).orElse(null);
    }

    // ✅ Update Incident Status
    public Incident updateIncidentStatus(Long id, String status) {
        Incident incident = incidentRepository.findById(id).orElse(null);
        if (incident != null) {
            incident.setStatus(status);
            return incidentRepository.save(incident);
        }
        return null;
    }

    // ✅ Update Full Incident
    public Incident updateIncident(Long id, Incident updatedIncident) {
        Incident incident = incidentRepository.findById(id).orElse(null);
        if (incident != null) {
            if (updatedIncident.getDescription() != null) {
                incident.setDescription(updatedIncident.getDescription());
            }
            if (updatedIncident.getLocation() != null) {
                incident.setLocation(updatedIncident.getLocation());
            }
            if (updatedIncident.getStatus() != null) {
                incident.setStatus(updatedIncident.getStatus());
            }
            if (updatedIncident.getResolutionNotes() != null) {
                incident.setResolutionNotes(updatedIncident.getResolutionNotes());
            }
            return incidentRepository.save(incident);
        }
        return null;
    }
}