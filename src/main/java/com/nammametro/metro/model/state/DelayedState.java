package com.nammametro.metro.model.state;

import com.nammametro.metro.model.Train;

public class DelayedState implements TrainState {

    @Override
    public void handle(Train train) {
        train.setStatus("Delayed");
    }

    @Override
    public String getStatus() {
        return "Delayed";
    }
}