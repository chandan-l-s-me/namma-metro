package com.nammametro.metro.model;

import com.nammametro.metro.model.state.TrainState;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Train {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonProperty("trainName")
    private String name;

    @JsonProperty("route")
    private String route;

    private Integer capacity;

    private String source;

    private String destination;

    private String status;

    @Transient
    private TrainState state;

    public void setState(TrainState state) {
        this.state = state;
        state.handle(this);
    }

    public void setStatus(String status) {
    this.status = status;
    }

    public String getStatus() {
    return status;
    }
}