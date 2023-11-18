package com.artillexstudios.axdeathchest.schedulers;

import com.artillexstudios.axdeathchest.chests.DeathChest;
import com.artillexstudios.axdeathchest.chests.SpawnedChests;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static com.artillexstudios.axdeathchest.AxDeathChest.EXECUTOR;

public class TickChests {

    public void start() {
        Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(() -> {
            EXECUTOR.execute(() -> {
                for (DeathChest deathChest : SpawnedChests.getChests()) {
                    deathChest.update();
                }
            });
        }, 100, 100, TimeUnit.MILLISECONDS);
    }
}
