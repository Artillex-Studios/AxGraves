package com.artillexstudios.axgraves.listeners;

import com.artillexstudios.axgraves.grave.Grave;
import com.artillexstudios.axgraves.grave.SpawnedGrave;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.jetbrains.annotations.NotNull;

public class PlayerInteractListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onInteract(@NotNull PlayerInteractEvent event) {
        if (event.getClickedBlock() == null) return;

        for (Grave grave : SpawnedGrave.getGraves()) {
            if (!grave.getLocation().getBlock().equals(event.getClickedBlock())) continue;
            grave.interact(event.getPlayer(), event.getHand());
            return;
        }
    }
}