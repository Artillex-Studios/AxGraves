package com.artillexstudios.axgraves.schedulers;

import com.artillexstudios.axgraves.config.Config;
import com.artillexstudios.axgraves.grave.SpawnedGraves;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static com.artillexstudios.axgraves.AxGraves.EXECUTOR;

public class SaveGraves {
    private static ScheduledFuture<?> future = null;

    public static void start() {
        if (future != null) future.cancel(true);

        if (!Config.SaveGraves.enabled) return;
        int seconds = Config.SaveGraves.autoSaveSeconds;
        if (seconds == -1) return;

        future = EXECUTOR.scheduleAtFixedRate(() -> {
            try {
                SpawnedGraves.saveToFile();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }, seconds, seconds, TimeUnit.SECONDS);
    }

    public static void stop() {
        if (future == null) return;
        future.cancel(true);
    }
}
