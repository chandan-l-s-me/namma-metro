package com.nammametro.metro.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

/**
 * RegularUser Entity - Extends User for regular passengers
 * GRASP: Specialization, Role assignment
 * SOLID: Liskov Substitution - RegularUser is-a User
 */
@Entity
@Table(name = "REGULAR_USER")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegularUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Phone number for contact
     */
    private String phoneNumber;

    /**
     * Account balance or wallet
     */
    private Double walletBalance;

    /**
     * Loyalty points for regular passengers
     */
    private Integer loyaltyPoints;

    public RegularUser(User user, String phoneNumber) {
        this.user = user;
        this.phoneNumber = phoneNumber;
        this.walletBalance = 0.0;
        this.loyaltyPoints = 0;
    }

    /**
     * Deduct fare from wallet
     */
    public boolean deductFare(Double amount) {
        if (walletBalance >= amount) {
            walletBalance -= amount;
            loyaltyPoints += (int)(amount / 10); // 1 point per 10 units spent
            return true;
        }
        return false;
    }

    /**
     * Add balance to wallet
     */
    public void addBalance(Double amount) {
        walletBalance += amount;
    }

    /**
     * Redeem loyalty points for discount
     */
    public Double redeemLoyaltyPoints(Integer points) {
        if (loyaltyPoints >= points) {
            loyaltyPoints -= points;
            return points * 0.5; // 1 point = 0.5 discount
        }
        return 0.0;
    }

    /**
     * Get user role
     */
    public UserRole getRole() {
        return UserRole.REGULAR_USER;
    }
}
