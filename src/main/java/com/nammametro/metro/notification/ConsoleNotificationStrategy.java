package com.nammametro.metro.notification;

import com.nammametro.metro.model.Notification;

/**
 * ConsoleNotificationStrategy - Concrete Strategy for console output
 * Broadcasts notifications to system console (useful for debugging/monitoring)
 */
public class ConsoleNotificationStrategy implements NotificationStrategy {
    
    @Override
    public boolean handle(Notification notification) {
        System.out.println("[CONSOLE] ⚡ " + notification.getTitle());
        System.out.println("[CONSOLE] → " + notification.getMessage());
        System.out.println("[CONSOLE] Type: " + notification.getType() + 
                          " | For User: " + notification.getUserId());
        return true;
    }
    
    @Override
    public String getStrategyName() {
        return "CONSOLE";
    }
}
