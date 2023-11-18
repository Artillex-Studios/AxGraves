package com.artillexstudios.axdeathchest.listeners;

import com.artillexstudios.axdeathchest.api.events.DeathChestPreSpawnEvent;
import com.artillexstudios.axdeathchest.api.events.DeathChestSpawnEvent;
import com.artillexstudios.axdeathchest.chests.DeathChest;
import com.artillexstudios.axdeathchest.chests.SpawnedChests;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.jetbrains.annotations.NotNull;

public class DeathListener implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onDeath(@NotNull PlayerDeathEvent event) {
        final Player player = event.getEntity();

        final DeathChest deathChest = new DeathChest(player.getLocation(), player, player.getInventory(), player.getTotalExperience());

        final DeathChestPreSpawnEvent deathChestPreSpawnEvent = new DeathChestPreSpawnEvent(player, deathChest);
        Bukkit.getPluginManager().callEvent(deathChestPreSpawnEvent);
        if (deathChestPreSpawnEvent.isCancelled()) return;

        event.setDroppedExp(0);
        event.getDrops().clear();

        SpawnedChests.addDeathChest(deathChest);

        final DeathChestSpawnEvent deathChestSpawnEvent = new DeathChestSpawnEvent(player, deathChest);
        Bukkit.getPluginManager().callEvent(deathChestSpawnEvent);
    }
}