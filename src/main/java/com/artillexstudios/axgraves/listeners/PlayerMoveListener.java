package com.artillexstudios.axgraves.listeners;

import com.artillexstudios.axgraves.utils.LocationUtils;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

public class PlayerMoveListener implements Listener {

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerMove(@NotNull PlayerMoveEvent event) {
        Player player = event.getPlayer();
        
        if (player.getGameMode() == GameMode.SPECTATOR || player.isFlying()) return;
        
        if (!hasPlayerMovedBlocks(event)) return;
        
        Location playerLoc = player.getLocation();
        
        if (isLocationSafeForTracking(playerLoc)) {
            LocationUtils.setLastSolidLocation(player, LocationUtils.roundLocation(playerLoc));
        }
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(@NotNull PlayerQuitEvent event) {
        LocationUtils.removeLastSolidLocation(event.getPlayer());
    }
    
    private boolean hasPlayerMovedBlocks(@NotNull PlayerMoveEvent event) {
        Location to = event.getTo();
        if (to == null) return false;
        
        Location from = event.getFrom();
        return to.getBlockX() != from.getBlockX() ||
               to.getBlockY() != from.getBlockY() ||
               to.getBlockZ() != from.getBlockZ();
    }
    
    private boolean isLocationSafeForTracking(@NotNull Location location) {
        World world = location.getWorld();
        if (world == null) return false;
        
        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();
        
        // Check void first
        if (y <= world.getMinHeight()) return false;
        
        // Get current block and block below
        Block currentBlock = world.getBlockAt(x, y, z);
        Block blockBelow = world.getBlockAt(x, y - 1, z);
        
        Material currentType = currentBlock.getType();
        Material belowType = blockBelow.getType();
        
        // Must have solid ground below (not lava)
        if (!belowType.isSolid() || belowType == Material.LAVA) return false;
        
        // Must not be standing in lava
        if (currentType == Material.LAVA) return false;
        
        return true;
    }
} 