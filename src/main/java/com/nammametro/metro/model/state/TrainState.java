package com.nammametro.metro.model.state;

import com.nammametro.metro.model.Train;

public interface TrainState {
    void handle(Train train);
    String getStatus();
}