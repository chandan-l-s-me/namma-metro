package com.nammametro.metro.notification;

import com.nammametro.metro.model.Notification;

/**
 * DatabaseNotificationStrategy - Concrete Strategy for persisting notifications
 * Stores notifications in database for user retrieval
 */
public class DatabaseNotificationStrategy implements NotificationStrategy {
    
    @Override
    public boolean handle(Notification notification) {
        // Database persistence is handled by NotificationService
        // This strategy confirms the notification is database-ready
        System.out.println("[DATABASE] Notification stored: " + notification.getTitle() + 
                          " - User: " + notification.getUserId());
        return true;
    }
    
    @Override
    public String getStrategyName() {
        return "DATABASE";
    }
}
