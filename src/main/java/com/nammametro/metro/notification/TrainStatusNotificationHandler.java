package com.nammametro.metro.notification;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.nammametro.metro.model.Notification;
import com.nammametro.metro.model.Train;
import com.nammametro.metro.model.RegularUser;
import com.nammametro.metro.repository.RegularUserRepository;
import com.nammametro.metro.service.NotificationService;
import java.util.ArrayList;
import java.util.List;

/**
 * TrainStatusNotificationHandler - Observer/Handler Pattern
 * Coordinates notifications when train status changes
 * 
 * SOLID Principles:
 * - Single Responsibility: Only handles train status notifications
 * - Dependency Injection: Depends on services, not direct instantiation
 * - Strategy Pattern: Supports multiple notification strategies
 */
@Component
public class TrainStatusNotificationHandler {
    
    @Autowired
    private NotificationService notificationService;
    
    @Autowired
    private RegularUserRepository regularUserRepository;
    
    private final List<NotificationStrategy> strategies = new ArrayList<>();
    
    /**
     * Constructor - Initialize notification strategies
     */
    public TrainStatusNotificationHandler() {
        // Add default strategies
        this.strategies.add(new ConsoleNotificationStrategy());
        this.strategies.add(new DatabaseNotificationStrategy());
    }
    
    /**
     * Add a notification strategy
     * Allows runtime addition of strategies
     */
    public void addStrategy(NotificationStrategy strategy) {
        strategies.add(strategy);
        System.out.println("[HANDLER] Added strategy: " + strategy.getStrategyName());
    }
    
    /**
     * Handle train status change notification
     * Notifies all regular users and stores notifications in database
     * 
     * @param train The train whose status changed
     * @param previousStatus Previous train status
     * @param newStatus New train status
     */
    public void handleTrainStatusChange(Train train, String previousStatus, String newStatus) {
        if (train == null) {
            System.err.println("[HANDLER ERROR] Train cannot be null");
            return;
        }
        
        try {
            // Create notification message
            String title = "Train Status Update: " + train.getName();
            String message = createNotificationMessage(train, previousStatus, newStatus);
            
            // Get all regular users to notify
            List<RegularUser> allUsers = regularUserRepository.findAll();
            
            System.out.println("[HANDLER] Processing train status change: " + train.getName() + 
                             " (" + previousStatus + " → " + newStatus + ")");
            System.out.println("[HANDLER] Notifying " + allUsers.size() + " users...");
            
            // Notify each user
            for (RegularUser regularUser : allUsers) {
                if (regularUser.getUser() != null) {
                    Long userId = regularUser.getUser().getId();
                    
                    // Create and store notification in database
                    Notification notification = notificationService.createNotification(
                        "TRAIN_STATUS_UPDATE",
                        title,
                        message,
                        userId,
                        String.valueOf(train.getId())
                    );
                    
                    // Execute all strategies
                    for (NotificationStrategy strategy : strategies) {
                        boolean result = strategy.handle(notification);
                        if (!result) {
                            System.err.println("[HANDLER] Strategy " + strategy.getStrategyName() + 
                                             " failed for user " + userId);
                        }
                    }
                }
            }
            
            System.out.println("[HANDLER] ✓ Train status change notifications completed");
            
        } catch (Exception e) {
            System.err.println("[HANDLER ERROR] Failed to handle train status change: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Create formatted notification message
     */
    private String createNotificationMessage(Train train, String previousStatus, String newStatus) {
        String routeName = train.getRoute() != null ? train.getRoute().getName() : "Unknown";
        
        return String.format(
            "Train %s on route %s has changed status from %s to %s. " +
            "Please check the app for more details.",
            train.getName(),
            routeName,
            previousStatus,
            newStatus
        );
    }
    
    /**
     * Handle train delay notification (special case)
     * This is a convenience method for delay-specific notifications
     */
    public void handleTrainDelay(Train train) {
        if (train == null) return;
        
        try {
            String title = "Train Delay Alert: " + train.getName();
            String routeName = train.getRoute() != null ? train.getRoute().getName() : "Unknown";
            String message = String.format(
                "Train %s on route %s is experiencing delays. " +
                "Passengers are advised to plan accordingly.",
                train.getName(),
                routeName
            );
            
            List<RegularUser> allUsers = regularUserRepository.findAll();
            
            System.out.println("[HANDLER] Processing train delay notification for: " + train.getName());
            System.out.println("[HANDLER] Notifying " + allUsers.size() + " users...");
            
            for (RegularUser regularUser : allUsers) {
                if (regularUser.getUser() != null) {
                    Long userId = regularUser.getUser().getId();
                    
                    Notification notification = notificationService.createNotification(
                        "TRAIN_DELAY",
                        title,
                        message,
                        userId,
                        String.valueOf(train.getId())
                    );
                    
                    for (NotificationStrategy strategy : strategies) {
                        strategy.handle(notification);
                    }
                }
            }
            
            System.out.println("[HANDLER] ✓ Train delay notifications completed");
            
        } catch (Exception e) {
            System.err.println("[HANDLER ERROR] Failed to handle train delay: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
