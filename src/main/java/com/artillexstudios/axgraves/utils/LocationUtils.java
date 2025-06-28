package com.artillexstudios.axgraves.utils;

import com.artillexstudios.axapi.libs.boostedyaml.block.implementation.Section;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.artillexstudios.axgraves.AxGraves.CONFIG;

public class LocationUtils {
    
    private static final Map<UUID, Location> lastSolidLocations = new HashMap<>();
    
    // Spiral search pattern with pre-calculated offsets
    private static final int[][] SPIRAL_OFFSETS = {
        {0, 0}, {1, 0}, {1, 1}, {0, 1}, {-1, 1}, {-1, 0}, {-1, -1}, {0, -1}, {1, -1},
        {2, 0}, {2, 1}, {2, 2}, {1, 2}, {0, 2}, {-1, 2}, {-2, 2}, {-2, 1}, {-2, 0},
        {-2, -1}, {-2, -2}, {-1, -2}, {0, -2}, {1, -2}, {2, -2}, {2, -1}
    };

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

    @NotNull
    public static Location roundLocation(@NotNull Location location) {
        return new Location(
            location.getWorld(),
            location.getBlockX() + 0.5,
            location.getBlockY(),
            location.getBlockZ() + 0.5,
            location.getYaw(),
            location.getPitch()
        );
    }

    public static void setLastSolidLocation(@NotNull Player player, @NotNull Location location) {
        lastSolidLocations.put(player.getUniqueId(), new Location(
            location.getWorld(),
            location.getX(),
            location.getY(),
            location.getZ(),
            location.getYaw(),
            location.getPitch()
        ));
    }

    @Nullable
    public static Location getLastSolidLocation(@NotNull Player player) {
        Location location = lastSolidLocations.get(player.getUniqueId());
        
        if (location == null) return null;
        
        World locationWorld = location.getWorld();
        if (locationWorld == null || !locationWorld.equals(player.getWorld())) {
            return null;
        }
        
        Block blockBelow = locationWorld.getBlockAt(
            location.getBlockX(),
            location.getBlockY() - 1,
            location.getBlockZ()
        );
        
        if (!blockBelow.getType().isSolid()) {
            return null;
        }
        
        return new Location(
            locationWorld,
            location.getX(),
            location.getY(),
            location.getZ(),
            location.getYaw(),
            location.getPitch()
        );
    }

    public static void removeLastSolidLocation(@NotNull Player player) {
        lastSolidLocations.remove(player.getUniqueId());
    }

    public static void clearAllLastSolidLocations() {
        lastSolidLocations.clear();
    }

    public static boolean isLocationSafe(@NotNull Location location) {
        World world = location.getWorld();
        if (world == null) return false;
        
        int blockX = location.getBlockX();
        int blockY = location.getBlockY();
        int blockZ = location.getBlockZ();
        
        Block currentBlock = world.getBlockAt(blockX, blockY, blockZ);
        Block blockBelow = world.getBlockAt(blockX, blockY - 1, blockZ);
        
        // Location is safe if current block is not solid and block below is solid
        return !currentBlock.getType().isSolid() && blockBelow.getType().isSolid();
    }

    public static boolean isVoid(@NotNull Location location) {
        World world = location.getWorld();
        return world != null && location.getY() <= world.getMinHeight();
    }

    public static boolean isLava(@NotNull Location location) {
        World world = location.getWorld();
        if (world == null) return false;
        
        return world.getBlockAt(
            location.getBlockX(),
            location.getBlockY(),
            location.getBlockZ()
        ).getType() == Material.LAVA;
    }

    public static boolean isInAir(@NotNull Location location) {
        World world = location.getWorld();
        if (world == null) return false;
        
        int blockX = location.getBlockX();
        int blockY = location.getBlockY();
        int blockZ = location.getBlockZ();
        
        // Check if current block is air
        if (world.getBlockAt(blockX, blockY, blockZ).getType() != Material.AIR) {
            return false;
        }
        
        // Check if no solid ground within 5 blocks below
        int minY = world.getMinHeight();
        int maxChecks = Math.min(5, blockY - minY);
        
        for (int i = 1; i <= maxChecks; i++) {
            if (world.getBlockAt(blockX, blockY - i, blockZ).getType().isSolid()) {
                return false;
            }
        }
        
        return true;
    }

    @NotNull
    public static Location findIntelligentGraveLocation(@NotNull Player player, @NotNull Location deathLocation) {
        boolean isDangerous = false;
        World world = deathLocation.getWorld();
        
        if (world == null) {
            isDangerous = true;
        } else {
            double y = deathLocation.getY();
            if (y <= world.getMinHeight()) {
                isDangerous = true; // void
            } else {
                Material blockType = world.getBlockAt(
                    deathLocation.getBlockX(),
                    deathLocation.getBlockY(),
                    deathLocation.getBlockZ()
                ).getType();
                
                if (blockType == Material.LAVA) {
                    isDangerous = true; // lava
                } else if (blockType == Material.AIR && isInAir(deathLocation)) {
                    isDangerous = true; // floating in air
                }
            }
        }
        
        // If dangerous, try to use last solid location
        if (isDangerous) {
            Location lastSolid = getLastSolidLocation(player);
            if (lastSolid != null && isLocationSafe(lastSolid)) {
                lastSolid.add(0, -0.5, 0);
                return lastSolid;
            }
        }
        
        // Try to find a safe location near the death location
        Location safeLocation = findNearestSafeLocation(deathLocation);
        if (safeLocation != null) {
            safeLocation.add(0, -0.5, 0);
            return safeLocation;
        }
        
        // Fallback to original behavior
        Location fallback = new Location(
            deathLocation.getWorld(),
            deathLocation.getX(),
            deathLocation.getY() - 0.5,
            deathLocation.getZ(),
            deathLocation.getYaw(),
            deathLocation.getPitch()
        );
        clampLocation(fallback);
        return fallback;
    }

    @Nullable
    private static Location findNearestSafeLocation(@NotNull Location center) {
        World world = center.getWorld();
        if (world == null) return null;
        
        int centerX = center.getBlockX();
        int centerZ = center.getBlockZ();
        
        // First check center location
        if (isLocationSafeAtCoords(world, centerX, centerZ, center.getBlockY())) {
            return roundLocation(center);
        }
        
        for (int[] offset : SPIRAL_OFFSETS) {
            int testX = centerX + offset[0];
            int testZ = centerZ + offset[1];
            
            Location groundLoc = findSolidGroundAtCoords(world, testX, testZ, center.getBlockY());
            if (groundLoc != null && isLocationSafeAtCoords(world, testX, testZ, groundLoc.getBlockY())) {
                return groundLoc;
            }
        }
        
        return null;
    }

    private static boolean isLocationSafeAtCoords(@NotNull World world, int x, int z, int y) {
        Block currentBlock = world.getBlockAt(x, y, z);
        Block blockBelow = world.getBlockAt(x, y - 1, z);
        
        return !currentBlock.getType().isSolid() && blockBelow.getType().isSolid();
    }

    @Nullable
    private static Location findSolidGroundAtCoords(@NotNull World world, int x, int z, int startY) {
        int maxSearchDistance = Math.min(50, world.getMaxHeight() - startY);
        int minY = world.getMinHeight();
        
        for (int i = 0; i < maxSearchDistance; i++) {
            int currentY = startY - i;
            if (currentY <= minY) break;
            
            Block blockBelow = world.getBlockAt(x, currentY - 1, z);
            Material type = blockBelow.getType();
            
            if (type.isSolid() && type != Material.LAVA) {
                return new Location(world, x + 0.5, currentY, z + 0.5);
            }
        }
        
        return null;
    }
}
