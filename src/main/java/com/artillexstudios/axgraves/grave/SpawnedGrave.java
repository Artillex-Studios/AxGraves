package com.artillexstudios.axgraves.grave;

import java.util.concurrent.ConcurrentLinkedQueue;

import static com.artillexstudios.axgraves.AxGraves.CONFIG;

public class SpawnedGrave {
    private static final ConcurrentLinkedQueue<Grave> graves = new ConcurrentLinkedQueue<>();

    public static void addGrave(Grave grave) {
        int graveLimit = CONFIG.getInt("grave-limit", -1);
        if (graveLimit != -1) {
            int num = 0;
            Grave oldest = grave;

            for (Grave grave2 : graves) {
                if (!grave2.getPlayer().equals(grave.getPlayer())) continue;
                if (oldest.getSpawned() > grave2.getSpawned()) oldest = grave2;
                num++;
            }

            if (num >= graveLimit) oldest.remove();
        }

        graves.add(grave);
    }

    public static void removeGrave(Grave grave) {
        graves.remove(grave);
    }

    public static ConcurrentLinkedQueue<Grave> getGraves() {
        return graves;
    }
}
