package com.nammametro.metro.model.state;

import com.nammametro.metro.model.Train;

public class ScheduledState implements TrainState {

    @Override
    public void handle(Train train) {
        train.setStatus("Scheduled");
    }

    @Override
    public String getStatus() {
        return "Scheduled";
    }
}