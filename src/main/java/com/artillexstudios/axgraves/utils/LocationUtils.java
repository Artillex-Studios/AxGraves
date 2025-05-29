package com.artillexstudios.axgraves.utils;

import com.artillexstudios.axapi.libs.boostedyaml.block.implementation.Section;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import static com.artillexstudios.axgraves.AxGraves.CONFIG;

public class LocationUtils {

    @NotNull
    public static Location getCenterOf(@NotNull Location location, boolean keepPitchYaw) {
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

    public static void clampLocation(Location location) {
        Section section = CONFIG.getSection("spawn-height-limits." + location.getWorld().getName());
        double min, max;
        if (section != null) {
            min = section.getDouble("min");
            max = section.getDouble("max");
        } else {
            switch (location.getWorld().getEnvironment()) {
                case NETHER, THE_END -> {
                    min = 0;
                    max = 255;
                }
                default -> {
                    min = -64;
                    max = 319;
                }
            }
        }
        location.setY(Math.clamp(location.getY(), min, max));
    }
}
