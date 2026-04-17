package com.nammametro.metro.service;

import com.nammametro.metro.model.*;
import com.nammametro.metro.repository.TicketRepository;
import com.nammametro.metro.repository.StationRepository;
import com.nammametro.metro.repository.TrainRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * TicketService - Business logic for ticket management
 * SOLID: Single Responsibility - only manages tickets
 * GRASP: Service, Façade, Information Expert
 */
@Service
@Transactional
public class TicketService {

    private final TicketRepository ticketRepository;
    private final StationRepository stationRepository;
    private final TrainRepository trainRepository;
    private final PricingService pricingService;
    private final RegularUserService regularUserService;

    public TicketService(TicketRepository ticketRepository,
                        StationRepository stationRepository,
                        TrainRepository trainRepository,
                        PricingService pricingService,
                        RegularUserService regularUserService) {
        this.ticketRepository = ticketRepository;
        this.stationRepository = stationRepository;
        this.trainRepository = trainRepository;
        this.pricingService = pricingService;
        this.regularUserService = regularUserService;
    }

    /**
     * Book a ticket for a regular user
     * GRASP: Creator - service creates tickets
     * Implements ticket pricing strategy
     */
    public Ticket bookTicket(Long regularUserId, Long trainId, Long sourceStationId,
                            Long destinationStationId, String passengerName, String travelDate) {
        // Validate inputs
        if (regularUserId == null || trainId == null || sourceStationId == null
                || destinationStationId == null) {
            throw new IllegalArgumentException("All parameters must be provided");
        }

        if (sourceStationId.equals(destinationStationId)) {
            throw new IllegalArgumentException("Source and destination cannot be the same");
        }

        // Fetch entities
        RegularUser regularUser = regularUserService.getRegularUserById(regularUserId)
                .orElseThrow(() -> new RuntimeException("Regular user not found"));

        Train train = trainRepository.findById(trainId)
                .orElseThrow(() -> new RuntimeException("Train not found"));

        Station sourceStation = stationRepository.findById(sourceStationId)
                .orElseThrow(() -> new RuntimeException("Source station not found"));

        Station destinationStation = stationRepository.findById(destinationStationId)
                .orElseThrow(() -> new RuntimeException("Destination station not found"));

        // Validate stations are on same route as train
        if (!sourceStation.getRoute().getId().equals(train.getRoute().getId()) ||
            !destinationStation.getRoute().getId().equals(train.getRoute().getId())) {
            throw new RuntimeException("Stations must be on the same route as the train");
        }

        // Calculate fare using pricing service
        Double calculatedFare = pricingService.calculateFare(sourceStationId, destinationStationId);

        // Check wallet balance
        if (!regularUserService.hasSufficientBalance(regularUserId, calculatedFare)) {
            throw new RuntimeException("Insufficient wallet balance. Required: " + calculatedFare +
                    ", Available: " + regularUser.getWalletBalance());
        }

        // Create ticket
        Ticket ticket = new Ticket(regularUser, passengerName, sourceStation,
                destinationStation, train, calculatedFare);
        ticket.setTravelDate(travelDate);
        ticket.setFinalPrice(calculatedFare);

        // Deduct fare from wallet
        regularUserService.deductFare(regularUserId, calculatedFare);

        // Save ticket
        Ticket savedTicket = ticketRepository.save(ticket);

        // Notify users
        notifyTicketBooked(savedTicket);

        return savedTicket;
    }

    /**
     * Get all tickets
     */
    public List<Ticket> getAllTickets() {
        return ticketRepository.findAll();
    }

    /**
     * Get ticket by ID
     */
    public Optional<Ticket> getTicketById(Long id) {
        return ticketRepository.findById(id);
    }

    /**
     * Cancel ticket and refund
     */
    public void cancelTicket(Long ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        if ("USED".equals(ticket.getStatus()) || "EXPIRED".equals(ticket.getStatus())) {
            throw new RuntimeException("Cannot cancel " + ticket.getStatus() + " ticket");
        }

        // Refund to wallet
        RegularUser regularUser = ticket.getRegularUser();
        regularUser.addBalance(ticket.getFinalPrice());

        ticket.cancel();
        ticketRepository.save(ticket);

        notifyTicketCancelled(ticket);
    }

    /**
     * Mark ticket as used (for check-in)
     */
    public void markTicketAsUsed(Long ticketId) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        ticket.markAsUsed();
        ticketRepository.save(ticket);
    }

    /**
     * Get tickets for a regular user
     */
    public List<Ticket> getTicketsForUser(Long regularUserId) {
        RegularUser regularUser = regularUserService.getRegularUserById(regularUserId)
                .orElseThrow(() -> new RuntimeException("Regular user not found"));

        return ticketRepository.findByRegularUser(regularUser);
    }

    /**
     * Apply loyalty discount to ticket
     */
    public void applyLoyaltyDiscount(Long ticketId, Integer loyaltyPoints) {
        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));

        Double discount = regularUserService.redeemLoyaltyPoints(
                ticket.getRegularUser().getId(), loyaltyPoints);

        ticket.applyDiscount(discount);
        ticketRepository.save(ticket);
    }

    /**
     * Delete ticket
     */
    public void deleteTicket(Long id) {
        ticketRepository.deleteById(id);
    }

    /**
     * Notify users about ticket booking
     */
    private void notifyTicketBooked(Ticket ticket) {
        System.out.println("Notification: Ticket booked for " + ticket.getPassengerName() +
                " from " + ticket.getSourceStationName() + " to " +
                ticket.getDestinationStationName() + ". Fare: " + ticket.getFare());
    }

    /**
     * Notify users about ticket cancellation
     */
    private void notifyTicketCancelled(Ticket ticket) {
        System.out.println("Notification: Ticket cancelled for " + ticket.getPassengerName() +
                ". Refund of " + ticket.getFinalPrice() + " credited to wallet.");
    }
}