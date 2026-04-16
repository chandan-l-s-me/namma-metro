package com.nammametro.metro.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.nammametro.metro.repository.TicketRepository;
import com.nammametro.metro.repository.TrainRepository;
import com.nammametro.metro.repository.IncidentRepository;

import java.util.HashMap;
import java.util.Map;

@Service
public class ReportService {

    @Autowired
    private TicketRepository ticketRepository;

    @Autowired
    private TrainRepository trainRepository;

    @Autowired
    private IncidentRepository incidentRepository;

    public Map<String, Object> generateReport() {
        Map<String, Object> report = new HashMap<>();

        report.put("totalTickets", ticketRepository.count());
        report.put("totalTrains", trainRepository.count());
        report.put("totalIncidents", incidentRepository.count());

        return report;
    }
}