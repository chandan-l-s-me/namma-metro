package com.nammametro.metro.controller;

import com.nammametro.metro.model.Ticket;
import com.nammametro.metro.service.TicketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import com.nammametro.metro.service.NotificationService;
@RestController
@RequestMapping("/tickets")
public class TicketController {

    @Autowired
    private NotificationService notificationService;

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    // Book Ticket
    @PostMapping
    public Ticket bookTicket(@RequestBody Ticket ticket) {

        // 🔥 ADD THIS (IMPORTANT)
        notificationService.notifyUsers(
                "Ticket booked for " + ticket.getPassengerName() +
                        " from " + ticket.getSource() +
                        " to " + ticket.getDestination()
        );

        return ticketService.bookTicket(ticket);
    }

    // Get All Tickets
    @GetMapping
    public List<Ticket> getAllTickets() {
        return ticketService.getAllTickets();
    }

    @DeleteMapping("/{id}")
    public String cancelTicket(@PathVariable Long id) {

        ticketService.deleteTicket(id);

        notificationService.notifyUsers("Ticket with ID " + id + " cancelled");

        return "Ticket cancelled!";
    }
}