package com.nammametro.metro.service;

import com.nammametro.metro.model.Incident;

public class IncidentFactory {

    public static Incident createIncident(String type, String description, String location) {

        Incident incident = new Incident();

        incident.setDescription(description);
        incident.setLocation(location);

        // optional logic (safe even if you don’t have priority field)
        // you can remove this if not needed

        return incident;
    }
}