package com.nammametro.metro.model;

import jakarta.persistence.*;
import lombok.*;
import com.nammametro.metro.model.state.TrainState;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Train {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String source;

    private String destination;

    private String status;

    @Transient
    private TrainState state;

    public void setState(TrainState state) {
        this.state = state;
        state.handle(this);
    }
}