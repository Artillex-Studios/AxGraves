package com.artillexstudios.axgraves.utils;

import com.artillexstudios.axapi.libs.boostedyaml.block.implementation.Section;
import com.artillexstudios.axapi.utils.logging.LogUtils;
import com.artillexstudios.axgraves.AxGraves;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import static com.artillexstudios.axgraves.AxGraves.CONFIG;

public class LocationUtils {

    @NotNull
    public static Location getCenterOf(@NotNull Location location, boolean keepYaw, boolean keepPitch) {
        Location loc = location.getBlock().getLocation().add(0.5, 0.5, 0.5);
        if (keepYaw) loc.setYaw(location.getYaw());
        if (keepPitch) loc.setPitch(location.getPitch());
        return loc;
    }

    public static int getNearestDirection(float x) {
        return Math.round(x / 90f) * 90;
    }

    public static void clampLocation(Location location) {
        World world = location.getWorld();
        boolean debug = AxGraves.isDebugMode();
        Section section = CONFIG.getSection("spawn-height-limits." + world.getName());
        double min, max;
        if (section != null) {
            if (debug) {
                LogUtils.debug("clamping location - reading from config for world '{}'", world.getName());
            }
            min = section.getDouble("min");
            max = section.getDouble("max");
        } else {
            if (debug) {
                LogUtils.debug("clamping location - not in config, using default for world '{}' (env: {})", world.getName(), world.getEnvironment().name());
            }
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
        double clampedY = Math.clamp(location.getY(), min, max);
        if (debug) {
            LogUtils.debug("clamping location - value: {} min: {} max: {} result: {}", location.getY(), min, max, clampedY);
        }
        location.setY(clampedY);
    }

    @NotNull
    public static String getWorldName(World world) {
        if (world == null) return "---";
        return CONFIG.getString("world-name." + world.getName(), world.getName());
    }
}
