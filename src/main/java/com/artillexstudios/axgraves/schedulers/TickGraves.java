package com.artillexstudios.axgraves.schedulers;

import com.artillexstudios.axgraves.grave.Grave;
import com.artillexstudios.axgraves.grave.SpawnedGraves;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static com.artillexstudios.axgraves.AxGraves.EXECUTOR;

public class TickGraves {
    private static ScheduledFuture<?> future = null;

    public static void start() {
        if (future != null) future.cancel(true);

        future = EXECUTOR.scheduleAtFixedRate(() -> {
            try {
                for (Grave grave : SpawnedGraves.getGraves()) {
                    grave.update();
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }, 100, 100, TimeUnit.MILLISECONDS);
    }

    public static void stop() {
        future.cancel(true);
    }
}
