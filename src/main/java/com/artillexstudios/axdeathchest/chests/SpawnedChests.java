package com.artillexstudios.axdeathchest.chests;

import java.util.ArrayList;

public class SpawnedChests {
    private static final ArrayList<DeathChest> chests = new ArrayList<>();

    public static void addDeathChest(DeathChest deathChest) {
        chests.add(deathChest);
    }

    public static void removeDeathChest(DeathChest deathChest) {
        chests.remove(deathChest);
    }

    public static ArrayList<DeathChest> getChests() {
        return chests;
    }
}
