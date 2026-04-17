package com.nammametro.metro.controller;

import com.nammametro.metro.model.RegularUser;
import com.nammametro.metro.model.User;
import com.nammametro.metro.service.RegularUserService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

/**
 * RegularUserController - API endpoints for regular user management
 * Handles registration, wallet, and loyalty points for regular passengers
 */
@RestController
@RequestMapping("/api/users/regular")
public class RegularUserController {

    private final RegularUserService regularUserService;

    public RegularUserController(RegularUserService regularUserService) {
        this.regularUserService = regularUserService;
    }

    /**
     * Register a new regular user
     * POST /api/users/regular/register
     */
    @PostMapping("/register")
    public RegularUser registerRegularUser(@RequestBody Map<String, String> request) {
        String name = request.get("name");
        String email = request.get("email");
        String password = request.get("password");
        String phoneNumber = request.get("phoneNumber");

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);

        return regularUserService.registerRegularUser(user, phoneNumber);
    }

    /**
     * Get regular user by ID
     * GET /api/users/regular/{id}
     */
    @GetMapping("/{id}")
    public RegularUser getRegularUserById(@PathVariable Long id) {
        return regularUserService.getRegularUserById(id)
                .orElseThrow(() -> new RuntimeException("Regular user not found"));
    }

    /**
     * Add wallet balance
     * POST /api/users/regular/{id}/wallet/add
     */
    @PostMapping("/{id}/wallet/add")
    public RegularUser addWalletBalance(
            @PathVariable Long id,
            @RequestBody Map<String, Double> request) {
        Double amount = request.get("amount");
        return regularUserService.addWalletBalance(id, amount);
    }

    /**
     * Get wallet balance
     * GET /api/users/regular/{id}/wallet/balance
     */
    @GetMapping("/{id}/wallet/balance")
    public Map<String, Double> getWalletBalance(@PathVariable Long id) {
        Double balance = regularUserService.getWalletBalance(id);
        return Map.of("balance", balance);
    }

    /**
     * Get loyalty points
     * GET /api/users/regular/{id}/loyalty-points
     */
    @GetMapping("/{id}/loyalty-points")
    public Map<String, Integer> getLoyaltyPoints(@PathVariable Long id) {
        Integer points = regularUserService.getLoyaltyPoints(id);
        return Map.of("loyalty_points", points);
    }

    /**
     * Redeem loyalty points
     * POST /api/users/regular/{id}/loyalty-points/redeem
     */
    @PostMapping("/{id}/loyalty-points/redeem")
    public Map<String, Object> redeemLoyaltyPoints(
            @PathVariable Long id,
            @RequestBody Map<String, Integer> request) {
        Integer points = request.get("points");
        Double discount = regularUserService.redeemLoyaltyPoints(id, points);
        return Map.of(
                "points_redeemed", points,
                "discount_received", discount
        );
    }

    /**
     * Check wallet balance for fare
     * GET /api/users/regular/{id}/can-afford?fare={fare}
     */
    @GetMapping("/{id}/can-afford")
    public Map<String, Object> canAffordFare(
            @PathVariable Long id,
            @RequestParam Double fare) {
        boolean canAfford = regularUserService.hasSufficientBalance(id, fare);
        Double balance = regularUserService.getWalletBalance(id);
        return Map.of(
                "fare", fare,
                "balance", balance,
                "can_afford", canAfford,
                "shortage", Math.max(0, fare - balance)
        );
    }
}
