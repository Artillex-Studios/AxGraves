package com.artillexstudios.axgraves.utils;

import com.artillexstudios.axapi.libs.boostedyaml.block.implementation.Section;
import com.artillexstudios.axgraves.grave.Grave;
import org.bukkit.Location;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

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

    @NotNull
    public static String getWorldName(World world) {
        if (world == null) return "---";
        return CONFIG.getString("world-name." + world.getName(), world.getName());
    }


    public static boolean isWithinDistanceToGrave(ConcurrentLinkedQueue<Grave> graves, Location location, int distance){

        for(Grave grave : graves){
            if(grave.getLocation().distance(location) < distance){
                return true;
            }
        }
        return false;
    }
}
