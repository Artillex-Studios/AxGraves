package com.artillexstudios.axgraves.utils;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

public class LocationUtils {

    @NotNull
    public static Location getCenterOf(@NotNull Location location) {
        location.setY(Math.round(location.getY()));
        return location.getBlock().getLocation().add(0.5, 0.5, 0.5);
    }

    public static int getNearestDirection(float x) {
        return Math.round(x / 90f) * 90;
    }
}
