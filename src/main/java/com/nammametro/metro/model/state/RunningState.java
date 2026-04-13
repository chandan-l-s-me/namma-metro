package com.nammametro.metro.model.state;

import com.nammametro.metro.model.Train;

public class RunningState implements TrainState {

    @Override
    public void handle(Train train) {
        train.setStatus("Running");
    }

    @Override
    public String getStatus() {
        return "Running";
    }
}