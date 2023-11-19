package com.artillexstudios.axgraves.grave;

import java.util.concurrent.ConcurrentLinkedQueue;

public class SpawnedGrave {
    private static final ConcurrentLinkedQueue<Grave> chests = new ConcurrentLinkedQueue<>();

    public static void addGrave(Grave grave) {
        chests.add(grave);
    }

    public static void removeGrave(Grave grave) {
        chests.remove(grave);
    }

    public static ConcurrentLinkedQueue<Grave> getGraves() {
        return chests;
    }
}
