package com.nammametro.metro.repository;

import com.nammametro.metro.model.Ticket;
import com.nammametro.metro.model.RegularUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    List<Ticket> findByRegularUser(RegularUser regularUser);

    List<Ticket> findByStatus(String status);
}