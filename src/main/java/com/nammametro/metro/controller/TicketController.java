package com.nammametro.metro.controller;

import com.nammametro.metro.model.Ticket;
import com.nammametro.metro.service.TicketService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tickets")
public class TicketController {

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    // Book Ticket
    @PostMapping
    public Ticket bookTicket(@RequestBody Ticket ticket) {
        return ticketService.bookTicket(ticket);
    }

    // Get All Tickets
    @GetMapping
    public List<Ticket> getAllTickets() {
        return ticketService.getAllTickets();
    }
}