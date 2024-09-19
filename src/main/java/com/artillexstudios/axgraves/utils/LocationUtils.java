package com.artillexstudios.axgraves.utils;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

public class LocationUtils {

    @NotNull
    public static Location getCenterOf(@NotNull Location location, boolean keepPitchYaw) {
        location.setY(Math.round(location.getY()));
        final Location loc = location.getBlock().getLocation().add(0.5, 0.5, 0.5);
        if (keepPitchYaw) {
            loc.setPitch(location.getPitch());
            loc.setYaw(location.getYaw());
        }
        return loc;
    }

    public static int getNearestDirection(float x) {
        return Math.round(x / 90f) * 90;
    }
}
