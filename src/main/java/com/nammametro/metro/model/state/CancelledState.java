package com.nammametro.metro.model.state;

import com.nammametro.metro.model.Train;


public class CancelledState implements TrainState {

    @Override
    public void handle(Train train) {
        train.setStatus("Cancelled");
    }

    @Override
    public String getStatus() {
        return "Cancelled";
    }
}