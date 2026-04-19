package com.nammametro.metro.notification;

import com.nammametro.metro.model.Notification;

/**
 * NotificationStrategy - Strategy Pattern for different notification types
 * Defines contract for notification handlers
 * 
 * SOLID Principles:
 * - Open/Closed: Open for extension (new strategies), closed for modification
 * - Dependency Inversion: Depend on abstraction, not concrete implementations
 */
public interface NotificationStrategy {
    
    /**
     * Handle notification delivery
     * @param notification Notification to be sent
     * @return true if notification was successfully handled
     */
    boolean handle(Notification notification);
    
    /**
     * Get strategy name for logging/debugging
     */
    String getStrategyName();
}
