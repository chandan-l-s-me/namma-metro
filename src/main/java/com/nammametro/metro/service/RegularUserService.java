package com.nammametro.metro.service;

import com.nammametro.metro.model.*;
import com.nammametro.metro.repository.RegularUserRepository;
import com.nammametro.metro.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * RegularUserService - Business logic for regular user management
 * SOLID: Single Responsibility - only manages regular users
 * GRASP: Service, Façade
 */
@Service
@Transactional
public class RegularUserService {

    private final RegularUserRepository regularUserRepository;
    private final UserRepository userRepository;

    public RegularUserService(RegularUserRepository regularUserRepository,
                             UserRepository userRepository) {
        this.regularUserRepository = regularUserRepository;
        this.userRepository = userRepository;
    }

    /**
     * Register a new regular user
     * GRASP: Creator - service creates regular users
     */
    public RegularUser registerRegularUser(User user, String phoneNumber) {
        if (user == null || user.getEmail() == null) {
            throw new IllegalArgumentException("User and email cannot be null");
        }

        // Save base user
        User savedUser = userRepository.save(user);

        // Create regular user profile
        RegularUser regularUser = new RegularUser(savedUser, phoneNumber);
        regularUser.setWalletBalance(0.0);
        regularUser.setLoyaltyPoints(0);

        return regularUserRepository.save(regularUser);
    }

    /**
     * Get regular user by ID
     */
    public Optional<RegularUser> getRegularUserById(Long id) {
        return regularUserRepository.findById(id);
    }

    /**
     * Get regular user by user ID
     */
    public Optional<RegularUser> getRegularUserByUserId(Long userId) {
        return regularUserRepository.findByUserId(userId);
    }

    /**
     * Add balance to wallet
     */
    public RegularUser addWalletBalance(Long regularUserId, Double amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }

        RegularUser regularUser = regularUserRepository.findById(regularUserId)
                .orElseThrow(() -> new RuntimeException("Regular user not found"));

        regularUser.addBalance(amount);
        return regularUserRepository.save(regularUser);
    }

    /**
     * Check if user has sufficient balance for fare
     */
    public boolean hasSufficientBalance(Long regularUserId, Double fare) {
        RegularUser regularUser = regularUserRepository.findById(regularUserId)
                .orElseThrow(() -> new RuntimeException("Regular user not found"));

        return regularUser.getWalletBalance() >= fare;
    }

    /**
     * Deduct fare from wallet
     */
    public boolean deductFare(Long regularUserId, Double fare) {
        RegularUser regularUser = regularUserRepository.findById(regularUserId)
                .orElseThrow(() -> new RuntimeException("Regular user not found"));

        if (regularUser.deductFare(fare)) {
            regularUserRepository.save(regularUser);
            return true;
        }
        return false;
    }

    /**
     * Get loyalty points balance
     */
    public Integer getLoyaltyPoints(Long regularUserId) {
        RegularUser regularUser = regularUserRepository.findById(regularUserId)
                .orElseThrow(() -> new RuntimeException("Regular user not found"));
        return regularUser.getLoyaltyPoints();
    }

    /**
     * Redeem loyalty points for discount
     */
    public Double redeemLoyaltyPoints(Long regularUserId, Integer points) {
        RegularUser regularUser = regularUserRepository.findById(regularUserId)
                .orElseThrow(() -> new RuntimeException("Regular user not found"));

        Double discount = regularUser.redeemLoyaltyPoints(points);
        regularUserRepository.save(regularUser);
        return discount;
    }

    /**
     * Get all users with loyalty points
     */
    public List<RegularUser> getUsersWithLoyaltyPoints(Integer minPoints) {
        return regularUserRepository.findByLoyaltyPointsGreaterThan(minPoints);
    }

    /**
     * Get wallet balance
     */
    public Double getWalletBalance(Long regularUserId) {
        RegularUser regularUser = regularUserRepository.findById(regularUserId)
                .orElseThrow(() -> new RuntimeException("Regular user not found"));
        return regularUser.getWalletBalance();
    }
}
