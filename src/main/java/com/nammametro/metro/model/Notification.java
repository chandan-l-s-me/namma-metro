package com.nammametro.metro.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "NOTIFICATION")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String type; // TICKET_BOOKING, TRAIN_DELAY, TRAIN_STATUS_UPDATE, TICKET_CANCELLATION, USER_REGISTRATION, INCIDENT_REPORT
    private String message;
    private String title;
    private boolean isRead = false;
    private LocalDateTime createdAt;
    private Long userId; // Associated user ID
    private String relatedId; // Ticket ID, Train ID, Incident ID, etc.

    public Notification() {
        this.createdAt = LocalDateTime.now();
    }

    public Notification(String type, String title, String message, Long userId) {
        this.type = type;
        this.title = title;
        this.message = message;
        this.userId = userId;
        this.createdAt = LocalDateTime.now();
        this.isRead = false;
    }

    // Getters & Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getRelatedId() {
        return relatedId;
    }

    public void setRelatedId(String relatedId) {
        this.relatedId = relatedId;
    }
}
