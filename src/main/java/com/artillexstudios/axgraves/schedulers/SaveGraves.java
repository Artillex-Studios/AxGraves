package com.artillexstudios.axgraves.schedulers;

import com.artillexstudios.axgraves.grave.SpawnedGraves;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import static com.artillexstudios.axgraves.AxGraves.CONFIG;
import static com.artillexstudios.axgraves.AxGraves.EXECUTOR;

public class SaveGraves {
    private static ScheduledFuture<?> future = null;

    public static void start() {
        if (future != null) future.cancel(true);

        if (!CONFIG.getBoolean("save-graves.enabled", true)) return;
        int seconds = CONFIG.getInt("save-graves.auto-save-seconds", 30);
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
