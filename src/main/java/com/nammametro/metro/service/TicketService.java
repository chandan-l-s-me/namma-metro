package com.nammametro.metro.service;

import com.nammametro.metro.model.Ticket;
import com.nammametro.metro.repository.TicketRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TicketService {

    private final TicketRepository ticketRepository;

    public TicketService(TicketRepository ticketRepository) {
        this.ticketRepository = ticketRepository;
    }

    // Book Ticket
    public Ticket bookTicket(Ticket ticket) {
        ticket.setFare(30.0); // temporary fixed fare
        return ticketRepository.save(ticket);
    }

    // Get All Tickets
    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }

    public void deleteTicket(Long id) {
        ticketRepository.deleteById(id);
    }
}