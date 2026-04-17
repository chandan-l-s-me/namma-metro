package com.nammametro.metro.controller;

import com.nammametro.metro.dto.TicketResponse;
import com.nammametro.metro.model.Ticket;
import com.nammametro.metro.service.TicketService;
import com.nammametro.metro.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * TicketController - API endpoints for ticket booking and management
 * Handles ticket booking, cancellation, and inquiry
 */
@RestController
@RequestMapping("/api/tickets")
public class TicketController {

    @Autowired
    private NotificationService notificationService;

    private final TicketService ticketService;

    public TicketController(TicketService ticketService) {
        this.ticketService = ticketService;
    }

    /**
     * Book a ticket
     * POST /api/tickets/book
     * Request body:
     * {
     *   "regularUserId": 1,
     *   "trainId": 1,
     *   "sourceStationId": 1,
     *   "destinationStationId": 5,
     *   "passengerName": "John Doe",
     *   "travelDate": "2024-12-25"
     * }
     */
    @PostMapping("/book")
    public TicketResponse bookTicket(@RequestBody Map<String, Object> request) {
        Long regularUserId = ((Number) request.get("regularUserId")).longValue();
        Long trainId = ((Number) request.get("trainId")).longValue();
        Long sourceStationId = ((Number) request.get("sourceStationId")).longValue();
        Long destinationStationId = ((Number) request.get("destinationStationId")).longValue();
        String passengerName = (String) request.get("passengerName");
        String travelDate = (String) request.get("travelDate");

        Ticket ticket = ticketService.bookTicket(regularUserId, trainId, sourceStationId,
                destinationStationId, passengerName, travelDate);

        // Notify users through observer pattern
        notifyUsers("Ticket booked for " + passengerName +
                " from " + ticket.getSourceStationName() +
                " to " + ticket.getDestinationStationName() +
                ". Fare: " + ticket.getFare());

        return toTicketResponse(ticket);
    }

    /**
     * Get all tickets
     * GET /api/tickets
     */
    @GetMapping
    public List<TicketResponse> getAllTickets() {
        return ticketService.getAllTickets().stream()
                .map(this::toTicketResponse)
                .collect(Collectors.toList());
    }

    /**
     * Get ticket by ID
     * GET /api/tickets/{id}
     */
    @GetMapping("/{id}")
    public TicketResponse getTicketById(@PathVariable Long id) {
        Ticket ticket = ticketService.getTicketById(id)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));
        return toTicketResponse(ticket);
    }

    /**
     * Get tickets for a user
     * GET /api/tickets/user/{regularUserId}
     */
    @GetMapping("/user/{regularUserId}")
    public List<TicketResponse> getTicketsForUser(@PathVariable Long regularUserId) {
        return ticketService.getTicketsForUser(regularUserId).stream()
                .map(this::toTicketResponse)
                .collect(Collectors.toList());
    }

    /**
     * Cancel ticket
     * POST /api/tickets/{id}/cancel
     */
    @PostMapping("/{id}/cancel")
    public Map<String, String> cancelTicket(@PathVariable Long id) {
        ticketService.cancelTicket(id);
        notifyUsers("Ticket cancelled and refund initiated");
        return Map.of("message", "Ticket cancelled successfully");
    }

    /**
     * Mark ticket as used (check-in)
     * POST /api/tickets/{id}/use
     */
    @PostMapping("/{id}/use")
    public Map<String, String> markTicketAsUsed(@PathVariable Long id) {
        ticketService.markTicketAsUsed(id);
        return Map.of("message", "Ticket marked as used");
    }

    /**
     * Apply loyalty discount
     * POST /api/tickets/{id}/apply-loyalty
     */
    @PostMapping("/{id}/apply-loyalty")
    public Ticket applyLoyaltyDiscount(
            @PathVariable Long id,
            @RequestBody Map<String, Integer> request) {
        Integer loyaltyPoints = request.get("loyaltyPoints");
        ticketService.applyLoyaltyDiscount(id, loyaltyPoints);
        return ticketService.getTicketById(id)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));
    }

    /**
     * Delete ticket
     * DELETE /api/tickets/{id}
     */
    @DeleteMapping("/{id}")
    public Map<String, String> deleteTicket(@PathVariable Long id) {
        ticketService.deleteTicket(id);
        return Map.of("message", "Ticket deleted successfully");
    }

    /**
     * Notify users (observer pattern)
     */
    private void notifyUsers(String message) {
        if (notificationService != null) {
            notificationService.notifyUsers(message);
        }
    }

    private TicketResponse toTicketResponse(Ticket ticket) {
        return new TicketResponse(
                ticket.getId(),
                ticket.getRegularUser() != null ? ticket.getRegularUser().getId() : null,
                ticket.getPassengerName(),
                ticket.getTrain() != null ? ticket.getTrain().getId() : null,
                ticket.getTrain() != null ? ticket.getTrain().getName() : null,
                ticket.getSourceStationName(),
                ticket.getDestinationStationName(),
                ticket.getFare(),
                ticket.getDiscount(),
                ticket.getFinalPrice(),
                ticket.getStatus(),
                ticket.getTravelDate(),
                ticket.getDistance()
        );
    }
}
