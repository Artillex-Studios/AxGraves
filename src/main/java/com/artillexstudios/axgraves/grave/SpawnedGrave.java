package com.artillexstudios.axgraves.grave;

import java.util.concurrent.ConcurrentLinkedQueue;

public class SpawnedGrave {
    private static final ConcurrentLinkedQueue<Grave> graves = new ConcurrentLinkedQueue<>();

    public static void addGrave(Grave grave) {
        graves.add(grave);
    }

    public static void removeGrave(Grave grave) {
        graves.remove(grave);
    }

    public static ConcurrentLinkedQueue<Grave> getGraves() {
        return graves;
    }
}
