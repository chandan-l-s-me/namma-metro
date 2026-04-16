package com.nammametro.metro.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.nammametro.metro.model.Incident;

public interface IncidentRepository extends JpaRepository<Incident, Long> {
}