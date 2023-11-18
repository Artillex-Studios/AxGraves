package com.artillexstudios.axdeathchest.utils;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;

public class LocationUtils {

    public static Location getCenterOf(@NotNull Location location) {
        location.setY(Math.round(location.getY()));
        return location.getBlock().getLocation().add(0.5, 0.5, 0.5);
    }
}
