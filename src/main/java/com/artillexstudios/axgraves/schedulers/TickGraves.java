package com.artillexstudios.axgraves.schedulers;

import com.artillexstudios.axapi.scheduler.Scheduler;
import com.artillexstudios.axgraves.grave.Grave;
import com.artillexstudios.axgraves.grave.SpawnedGraves;
import org.bukkit.entity.HumanEntity;

import java.util.ArrayList;
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

        Scheduler.get().runTimer(() -> {
            for (Grave grave : SpawnedGraves.getGraves()) {
                for (HumanEntity viewer : new ArrayList<>(grave.getGui().getInventory().getViewers())) {
                    if (viewer.getLocation().distanceSquared(grave.getLocation()) <= 49) continue;
                    viewer.closeInventory();
                }
            }
        }, 20, 20);
    }

    public static void stop() {
        if (future == null) return;
        future.cancel(true);
    }
}
