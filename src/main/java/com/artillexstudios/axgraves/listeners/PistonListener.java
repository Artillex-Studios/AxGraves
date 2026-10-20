package com.artillexstudios.axgraves.listeners;

import com.artillexstudios.axgraves.grave.SpawnedGraves;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PistonListener implements Listener {

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onExtend(@NotNull BlockPistonExtendEvent event) {
        if (!SpawnedGraves.hasGraves(event.getBlock().getWorld())) return;
        if (SpawnedGraves.isProtected(event.getBlock().getRelative(event.getDirection())) || touchesGrave(event.getBlocks(), event.getDirection())) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onRetract(@NotNull BlockPistonRetractEvent event) {
        if (!SpawnedGraves.hasGraves(event.getBlock().getWorld())) return;
        if (touchesGrave(event.getBlocks(), event.getDirection())) {
            event.setCancelled(true);
        }
    }

    private boolean touchesGrave(@NotNull List<Block> blocks, @NotNull BlockFace direction) {
        for (Block block : blocks) {
            if (SpawnedGraves.isProtected(block)) return true;
            if (SpawnedGraves.isProtected(block.getRelative(direction))) return true;
        }
        return false;
    }
}
