package com.artillexstudios.axgraves.schedulers;

import com.artillexstudios.axgraves.grave.Grave;
import com.artillexstudios.axgraves.grave.SpawnedGraves;

import java.util.concurrent.TimeUnit;

import static com.artillexstudios.axgraves.AxGraves.EXECUTOR;

public class TickGraves {

    public void start() {
        EXECUTOR.scheduleAtFixedRate(() -> {
            for (Grave grave : SpawnedGraves.getGraves()) {
                grave.update();
            }
        }, 100, 100, TimeUnit.MILLISECONDS);
    }
}
